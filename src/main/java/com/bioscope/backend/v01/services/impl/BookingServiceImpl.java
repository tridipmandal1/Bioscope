package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.*;
import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.TicketMapper;
import com.bioscope.backend.v01.models.user.PaymentVerificationRequest;
import com.bioscope.backend.v01.models.user.TicketModel;
import com.bioscope.backend.v01.repos.*;
import com.bioscope.backend.v01.services.iface.BookingService;
import com.bioscope.backend.v01.services.iface.BucketService;
import com.bioscope.backend.v01.services.iface.QRCodeService;
import com.bioscope.backend.v01.utils.EncryptionUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.razorpay.Utils;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ShowSeatRepository showSeatRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SeatEventRepository seatEventRepository;
    private final ShowRepository showRepository;
    private final TicketMapper ticketMapper;
    private final QRCodeService qrCodeService;
    private final BucketService bucketService;
    private final EncryptionUtil encryptionUtil;
    private final RazorpayClient razorpayClient;
    private final PassCategoryRepository passCategoryRepository;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    public BookingServiceImpl(
            RedisTemplate<String, String> redisTemplate,
            ShowSeatRepository showSeatRepository,
            UserRepository userRepository,
            TicketRepository ticketRepository,
            SeatEventRepository seatEventRepository,
            ShowRepository showRepository,
            TicketMapper ticketMapper,
            QRCodeService qrCodeService,
            BucketService bucketService,
            EncryptionUtil encryptionUtil,
            RazorpayClient razorpayClient, PassCategoryRepository passCategoryRepository){
        this.redisTemplate = redisTemplate;
        this.showSeatRepository = showSeatRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.seatEventRepository = seatEventRepository;
        this.showRepository = showRepository;
        this.ticketMapper = ticketMapper;
        this.qrCodeService = qrCodeService;
        this.bucketService = bucketService;
        this.encryptionUtil = encryptionUtil;
        this.razorpayClient = razorpayClient;
        this.passCategoryRepository = passCategoryRepository;
    }

    @Override
    @Transactional
    public TicketModel bookEntryPass(String showId, String category, Integer quantity, Double amount) {
        UserEntity user = getUserContext();

        if (showId == null || category == null || quantity == null || quantity <= 0 || amount == null) {
            throw new IllegalArgumentException("ShowId, Category, Quantity, and Amount must not be null");
        }

        UUID showUuid = UUID.fromString(showId);
        ShowEntity show = showRepository.findById(showUuid).orElseThrow(
                () -> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        if (show.getTicketPrice() == null) {
            throw new IllegalArgumentException("Show is not of type standing");
        }

        PassCategoryEntity passCategory =
                show.getTicketPrice()
                        .stream()
                        .filter(cat -> cat
                                .getCategory()
                                .equals(category.toUpperCase())).findFirst().orElseThrow(
                                () -> new ResourceNotFoundException("PassCategory", "Category", category)
                        );

        // Reserve passes in Redis
        String passKey = "show:" + showUuid + "category:" + category.toUpperCase()  + ":passes";
        if (!reservePassesInRedis(passKey, passCategory.getCapacity(), quantity)) {
            throw new IllegalArgumentException("Not enough passes available");
        }

        UUID userId = user.getId();
        seatEventRepository.save(new SeatEventEntity("PassReserved", showUuid, quantity, userId));

        // Create ticket with PENDING status
        TicketEntity ticket = createEntryPassTicket(show, category, quantity, user);
        ticket.setPaymentStatus("PENDING");
        ticket.setAmount(amount);
        ticket = ticketRepository.save(ticket);

        // Create Razorpay order
        try {
            String orderId = createRazorpayOrder(ticket.getId().toString(), amount);
            ticket.setOrderId(orderId);
            ticketRepository.save(ticket);
        } catch (Exception e) {
            rollbackPassReservation(passKey, quantity);
            seatEventRepository.save(new SeatEventEntity("PassReservationFailed", showUuid, quantity, userId));
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }

        return ticketMapper.entityToModel(ticket);
    }

    @Override
    @Transactional
    public TicketModel bookSeats(String showId, List<String> showSeatIds, Double amount) {
        UserEntity user = getUserContext();

        if (showId == null || showSeatIds.isEmpty() || amount == null) {
            throw new IllegalArgumentException("ShowId, ShowSeatIds, and Amount must not be null");
        }

        ShowEntity show = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                () -> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        if (!show.getArrangementType().equals(ArrangementType.SITTING)) {
            throw new IllegalArgumentException("Show is not of type sitting");
        }

        List<UUID> seatIds = showSeatIds.stream().map(UUID::fromString).toList();

        if (!reserveSeatsInRedis(UUID.fromString(showId), user.getId(), seatIds)) {
            throw new RuntimeException("One or more seats are already booked");
        }

        seatIds.forEach(seatId -> {
            seatEventRepository.save(new SeatEventEntity(
                    "SeatReserved",
                    show.getShowId(),
                    seatId,
                    user.getId()
            ));
        });

        // Create ticket with PENDING status
        TicketEntity ticket = createTicket(show, seatIds);
        ticket.setPaymentStatus("PENDING");
        ticket.setAmount(amount);
        ticket = ticketRepository.save(ticket);

        // Create Razorpay order
        try {
            String orderId = createRazorpayOrder(ticket.getId().toString(), amount);
            ticket.setOrderId(orderId);
            ticketRepository.save(ticket);
        } catch (Exception e) {
            rollbackReservations(UUID.fromString(showId), seatIds);
            seatIds.forEach(seatId -> {
                seatEventRepository.save(new SeatEventEntity(
                        "SeatReservationFailed",
                        show.getShowId(),
                        seatId,
                        user.getId()
                ));
            });
            throw new RuntimeException("Failed to create payment order: " + e.getMessage());
        }

        return ticketMapper.entityToModel(ticket);
    }

    @Override
    @Transactional
    public boolean verifyPayment(PaymentVerificationRequest verificationRequest) {
        try {
            log.info(verificationRequest.toString());
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", verificationRequest.getOrderId());
            attributes.put("razorpay_payment_id", verificationRequest.getPaymentId());
            attributes.put("razorpay_signature", verificationRequest.getSignature());
            Utils.verifyPaymentSignature(attributes, razorpayKeySecret);

            // Find ticket
            TicketEntity ticket = ticketRepository.findById(UUID.fromString(verificationRequest.getTicketId())).orElseThrow(
                    () -> new ResourceNotFoundException("Ticket", "TicketId", verificationRequest.getTicketId())
            );

            ShowEntity show = showRepository.findById(UUID.fromString(ticket.getShowId())).orElseThrow(
                    () -> new ResourceNotFoundException("Show", "ShowId", ticket.getShowId())
            );
                var category = ticket.getCategory();
                PassCategoryEntity passCategory =
                        show.getTicketPrice()
                                .stream()
                                .filter(pass -> pass.getCategory()
                                        .equals(category)).findFirst().orElseThrow(
                                        () -> new ResourceNotFoundException("PassCategory", "Category", category)
                                );
            // Confirm booking
            if (show.getMovie() == null) {
                String passKey = "show:" + show.getShowId() + "category:" + category.toUpperCase()  + ":passes";
                confirmPassesInRedis(passKey);
                passCategory.setReserved((passCategory.getReserved() == null ? 0 : passCategory.getReserved()) + ticket.getAllowedPersons());
                show.setBookings((show.getBookings() == null ? 0 : show.getBookings()) + ticket.getAllowedPersons());
                seatEventRepository.save(new SeatEventEntity("PassBooked", show.getShowId(), ticket.getAllowedPersons(), ticket.getUser().getId()));
            } else {
                List<UUID> seatIds = ticket.getShowSeats().stream().map(ShowSeatEntity::getId).toList();
                confirmBookingInRedis(show.getShowId(), seatIds);
                seatIds.forEach(seatId -> {
                    seatEventRepository.save(new SeatEventEntity(
                            "SeatBooked",
                            show.getShowId(),
                            seatId,
                            ticket.getUser().getId()
                    ));
                });
            }

            // Generate QR code
            String token = encryptionUtil.encrypt(ticket.getId().toString());
            try {
                MultipartFile qrCode = qrCodeService.generateQRCode(token, ticket.getId().toString());
                ticket.setTicketQRCode(bucketService.uploadFile(qrCode));
            } catch (Exception e) {
                log.info("Facing error generating QR");
                throw new RuntimeException("Failed to generate QR code: " + e.getMessage());
            }

            // Update ticket
            ticket.setPaymentId(verificationRequest.getPaymentId());
            ticket.setPaymentStatus("SUCCESS");
            ticketRepository.save(ticket);
            showRepository.save(show);
            userRepository.save(ticket.getUser());
            log.info("Payment verification successful");
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            // Rollback on failure
            rollbackOnPaymentFailure(verificationRequest.getTicketId());
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(String showId, List<String> seatIds, String ticketId) {
        ShowEntity show = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                () -> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        TicketEntity ticket = ticketRepository.findById(UUID.fromString(ticketId)).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "TicketId", ticketId)
        );

        if (!ticket.getUser().getId().equals(getUserContext().getId())) {
            throw new IllegalArgumentException("You are not authorized to cancel this ticket");
        }

        if (!ticket.getPaymentStatus().equals("SUCCESS")) {
            throw new IllegalArgumentException("Cannot cancel a non-confirmed booking");
        }

        List<UUID> seatUuids = seatIds.stream().map(UUID::fromString).toList();

        // Initiate refund with 10% deduction
        initiateRefund(ticket);

        // Update database and Redis
        seatUuids.forEach(seatId -> {
            if (!showSeatRepository.isSeatShowAvailable(seatId)) {
                showSeatRepository.cancelSeatReservation(seatId);
            }
            redisTemplate.delete("show:" + showId + ":seat:" + seatId);
            seatEventRepository.save(new SeatEventEntity(
                    "ReservationCancelled",
                    show.getShowId(),
                    seatId,
                    getUserContext().getId()
            ));
        });

        ticket.getShowSeats().clear();
        ticket.setPaymentStatus("CANCELLED");
        ticketRepository.save(ticket);
        ticketRepository.delete(ticket);
    }

    @Override
    @Transactional
    public void cancelEntryPass(TicketModel ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket must not be null");
        }

        TicketEntity ticketEntity = ticketRepository.findById(UUID.fromString(ticket.getId())).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "TicketId", ticket.getId())
        );

        if (!ticketEntity.getUser().getId().equals(getUserContext().getId())) {
            throw new IllegalArgumentException("You are not authorized to cancel this ticket");
        }

        if (!ticketEntity.getPaymentStatus().equals("SUCCESS")) {
            throw new IllegalArgumentException("Cannot cancel a non-confirmed booking");
        }

        ShowEntity showEntity = showRepository.findById(UUID.fromString(ticketEntity.getShowId())).orElseThrow(
                () -> new ResourceNotFoundException("Show", "ShowId", ticketEntity.getShowId())
        );

        // Initiate refund with 10% deduction
        initiateRefund(ticketEntity);
        var category = ticket.getCategory();
        // Update database and Redis
        String passKey = "show:" + showEntity.getShowId() + "category:" + category.toUpperCase()  + ":passes";
        rollbackPassReservation(passKey, ticketEntity.getAllowedPersons());
        PassCategoryEntity passCategory =
                showEntity.getTicketPrice()
                        .stream()
                        .filter(cat -> cat
                                .getCategory()
                                .equals(category.toUpperCase())).findFirst().orElseThrow(
                                () -> new ResourceNotFoundException("PassCategory", "Category", category)
                        );
        passCategory.setReserved(passCategory.getReserved() - ticketEntity.getAllowedPersons());
        showEntity.setBookings(showEntity.getBookings() - ticketEntity.getAllowedPersons());
        ticketEntity.setPaymentStatus("CANCELLED");
        ticketRepository.save(ticketEntity);
        ticketRepository.delete(ticketEntity);
        passCategoryRepository.save(passCategory);
        showRepository.save(showEntity);
        seatEventRepository.save(new SeatEventEntity("PassCancelled", showEntity.getShowId(), ticketEntity.getAllowedPersons(), getUserContext().getId()));
    }

    private String createRazorpayOrder(String ticketId, Double amount)
            throws RazorpayException, JSONException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", ticketId);
        Order order = razorpayClient.orders.create(orderRequest);
        return order.get("id");
    }

    private void initiateRefund(TicketEntity ticket) {
        try {
            double refundAmount = ticket.getAmount() * 0.9; // 10% deduction
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("payment_id", ticket.getPaymentId());
            refundRequest.put("amount", (int) (refundAmount * 100)); // Amount in paise
            refundRequest.put("speed", "normal");
            Refund refund = razorpayClient.payments.refund(refundRequest);
            log.info("Refund initialized for  {}", refund);
        } catch (RazorpayException | JSONException e) {
            throw new RuntimeException("Failed to initiate refund: " + e.getMessage());
        }
    }

    private void rollbackOnPaymentFailure(String ticketId) {
        TicketEntity ticket = ticketRepository.findById(UUID.fromString(ticketId)).orElse(null);
        if (ticket != null) {
            ShowEntity show = showRepository.findById(UUID.fromString(ticket.getShowId())).orElse(null);
            if (show != null) {
                if (show.getArrangementType().equals(ArrangementType.STANDING)) {
                    String passKey = "show:" + show.getShowId() +
                            "category:" + ticket.getCategory().toUpperCase()  + ":passes";
                    rollbackPassReservation(passKey, ticket.getAllowedPersons());
                    seatEventRepository.save(new SeatEventEntity("PassReservationFailed", show.getShowId(), ticket.getAllowedPersons(), ticket.getUser().getId()));
                } else {
                    List<UUID> seatIds = ticket.getShowSeats().stream().map(ShowSeatEntity::getId).toList();
                    rollbackReservations(show.getShowId(), seatIds);
                    seatIds.forEach(seatId -> {
                        seatEventRepository.save(new SeatEventEntity(
                                "SeatReservationFailed",
                                show.getShowId(),
                                seatId,
                                ticket.getUser().getId()
                        ));
                    });
                }
            }
            ticket.setPaymentStatus("FAILED");
            ticketRepository.save(ticket);
        }
    }

    private boolean reservePassesInRedis(String passKey, int capacity, int quantity) {
        String currentReserved = redisTemplate.opsForValue().get(passKey);
        if (currentReserved == null) {
            redisTemplate.opsForValue().setIfAbsent(passKey, "0");
        }

        Long newReserved = redisTemplate.opsForValue().increment(passKey, quantity);
        if (newReserved == null || newReserved > capacity) {
            redisTemplate.opsForValue().decrement(passKey, quantity);
            return false;
        }

        redisTemplate.expire(passKey, 10, TimeUnit.MINUTES);
        return true;
    }

    private void rollbackPassReservation(String passKey, int quantity) {
        redisTemplate.opsForValue().decrement(passKey, quantity);
    }

    private void confirmPassesInRedis(String passKey) {
        redisTemplate.persist(passKey);
    }

    private TicketEntity createEntryPassTicket(ShowEntity show, String category, Integer quantity, UserEntity user) {
        TicketEntity ticket = new TicketEntity();
        ticket.setShowName(show.getShowName());
        ticket.setDate(Date.from(show.getShowDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ticket.setStartTime(show.getShowTime());
        ticket.setUser(user);
        ticket.setHostId(show.getUser().getId().toString());
        ticket.setCategory(category);
        ticket.setAllowedPersons(quantity);
        ticket.setShowId(show.getShowId().toString());
        return ticket;
    }

    private boolean reserveSeatsInRedis(UUID showId, UUID userId, List<UUID> showSeatIds) {
        for (UUID seatId : showSeatIds) {
            String key = "show:" + showId + ":seat:" + seatId;
            String value = "reserved:" + userId;

            Boolean reserved = redisTemplate.opsForValue().setIfAbsent(key, value);
            if (!Boolean.TRUE.equals(reserved)) {
                rollbackReservations(showId, showSeatIds.subList(0, showSeatIds.indexOf(seatId)));
                return false;
            }
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
        return true;
    }

    private void confirmBookingInRedis(UUID showId, List<UUID> showSeatIds) {
        showSeatIds.forEach(seatId -> {
            String key = "show:" + showId + ":seat:" + seatId;
            redisTemplate.opsForValue().set(key, "booked");
            redisTemplate.persist(key);
        });
    }

    private TicketEntity createTicket(ShowEntity showEntity, List<UUID> showSeatIds) {
        UserEntity user = getUserContext();
        TicketEntity ticket = new TicketEntity();
        ticket.setShowName(showEntity.getShowName());
        ticket.setDate(Date.from(showEntity.getShowDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ticket.setStartTime(showEntity.getShowTime());
        ticket.setUser(user);
        ticket.setHostId(showEntity.getUser().getId().toString());
        ticket.setShowId(showEntity.getShowId().toString());

        List<ShowSeatEntity> showSeats = showSeatRepository.findByShowShowId(showEntity.getShowId())
                .stream()
                .filter(seat -> showSeatIds.contains(seat.getId()))
                .peek(seat -> seat.setSeatStatus(SeatStatus.BOOKED))
                .toList();

        showSeats.forEach(ticket::addShowSeat);
        return ticket;
    }

    private void rollbackReservations(UUID showId, List<UUID> reservedSeatIds) {
        reservedSeatIds.forEach(seatId ->
                redisTemplate.delete("show:" + showId + ":seat:" + seatId)
        );
    }

    private UserEntity getUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Failed to fetch user: " + username));
    }
}
