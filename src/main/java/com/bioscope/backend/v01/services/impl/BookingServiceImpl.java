package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.*;
import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.TicketMapper;
import com.bioscope.backend.v01.models.user.TicketModel;
import com.bioscope.backend.v01.repos.*;
import com.bioscope.backend.v01.security.JwtProvider;
import com.bioscope.backend.v01.services.iface.BookingService;
import com.bioscope.backend.v01.services.iface.BucketService;
import com.bioscope.backend.v01.services.iface.QRCodeService;
import com.bioscope.backend.v01.utils.EncryptionUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public BookingServiceImpl(
            RedisTemplate<String, String> redisTemplate,
            ShowSeatRepository showSeatRepository,
            UserRepository userRepository,
            TicketRepository ticketRepository,
            SeatEventRepository seatEventRepository,
            ShowRepository showRepository,
            TicketMapper ticketMapper,
            QRCodeService qrCodeService,
            BucketService bucketService, EncryptionUtil encryptionUtil){
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
    }


    @Override
    @Transactional
    public TicketModel bookEntryPass(String showId, String category, Integer quantity) {
        UserEntity user = this.getUserContext();

        if (showId == null || category == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("ShowId, Category and Quantity must not be null");
        }

        UUID showUuid = UUID.fromString(showId);
        ShowEntity show = showRepository.findById(showUuid).orElseThrow(
                ()-> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        if (!show.getArrangementType().equals(ArrangementType.STANDING)){
            throw new IllegalArgumentException("Show is not of type standing");
        }

        // Step 1: Check and reserve passes in Redis
        String passKey = "show:" + showUuid + ":passes";
        if (!reservePassesInRedis(passKey, show.getCapacity(), quantity)) {
            throw new IllegalArgumentException("Not enough passes available");
        }

        UUID userId = user.getId();
        seatEventRepository.save(new SeatEventEntity("PassReserved", showUuid, quantity, userId));

        TicketEntity ticket = createEntryPassTicket(show, category, quantity, user);


        confirmPassesInRedis(passKey);
        seatEventRepository.save(new SeatEventEntity("PassBooked", showUuid, quantity, userId));

        // Step 5: Update show entity
        show.setReserved((show.getReserved() == null ? 0 : show.getReserved()) + quantity);
        show.setBookings((show.getBookings() == null ? 0 : show.getBookings()) + quantity);

        String token = encryptionUtil.encrypt(ticket.getId().toString());

        try {
            MultipartFile qrCode = qrCodeService.generateQRCode(token, ticket.getId().toString());
            ticket.setTicketQRCode(bucketService.uploadFile(qrCode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        showRepository.save(show);

        user.addTicket(ticket);
        userRepository.save(user);

        return ticketMapper.entityToModel(ticketRepository.save(ticket));
    }

    private boolean reservePassesInRedis(String passKey, int capacity, int quantity) {

        String currentReserved = redisTemplate.opsForValue().get(passKey);
        if (currentReserved == null) {
            redisTemplate.opsForValue().setIfAbsent(passKey, "0");
        }

        Long newReserved = redisTemplate.opsForValue().increment(passKey, quantity);

        if( newReserved == null || newReserved > capacity) {
            redisTemplate.opsForValue().decrement(passKey, quantity);
            return false;
        }

        redisTemplate.expire(passKey, 10, TimeUnit.MINUTES);
        return true;
    }

    private void confirmPassesInRedis(String pasKey) {
        redisTemplate.persist(pasKey);
    }

    private TicketEntity createEntryPassTicket(ShowEntity show, String category,
                                               Integer quantity, UserEntity user) {
        TicketEntity ticket = new TicketEntity();
        ticket.setShowName(show.getShowName());
        ticket.setDate(Date.from(show.getShowDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ticket.setStartTime(show.getShowTime());
        ticket.setUser(user);
        ticket.setHostId(show.getUser().getId().toString());
        ticket.setCategory(category);
        ticket.setAllowedPersons(quantity);
        ticket.setShowId(show.getShowId().toString());
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketModel bookSeats(String showId, List<String> showSeatIds) {
        UserEntity user = this.getUserContext();

        if (showId == null || showSeatIds.isEmpty()) {
            throw new IllegalArgumentException("ShowId and ShowSeatIds must not be null");
        }

        ShowEntity show = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                ()-> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        if (!show.getArrangementType().equals(ArrangementType.SITTING)){
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

        List<UUID> seatUuids = showSeatIds.stream().map(UUID::fromString).toList();
        TicketEntity ticket = createTicket(show, seatIds);
        this.confirmBookingInRedis(UUID.fromString(showId), seatUuids);

        seatIds.forEach(seatId -> {
            seatEventRepository.save(new SeatEventEntity(
                    "SeatBooked",
                    show.getShowId(),
                    seatId,
                    user.getId()
            ));
        });

        return ticketMapper.entityToModel(ticket);
    }

    @Override
    @Transactional
    public void cancelBooking(String showId, List<String> seatIds, String ticketId) {

        if (showId == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("ShowId and ShowSeatIds must not be null");
        }

        ShowEntity show = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                ()-> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        TicketEntity ticket = ticketRepository.findById(UUID.fromString(ticketId)).orElseThrow(
                ()-> new ResourceNotFoundException("Ticket", "TicketId", ticketId)
        );
        if (!ticket.getUser().getId().equals(this.getUserContext().getId())){
            throw new IllegalArgumentException("You are not authorized to cancel this ticket");
        }
        List<UUID> seatUuids = seatIds.stream().map(UUID::fromString).toList();

        seatUuids.forEach(seatId ->{
            if(!showSeatRepository.isSeatShowAvailable(seatId)){
                showSeatRepository.cancelSeatReservation(seatId);
            }
        });
        seatUuids.forEach(seatId -> {
            seatEventRepository.save(new SeatEventEntity(
                    "ReservationExpired",
                    show.getShowId(),
                    seatId,
                    this.getUserContext().getId()
            ));
        });

        rollbackReservations(UUID.fromString(showId), seatUuids);
            ticket.getShowSeats().clear();
            ticketRepository.save(ticket);
            ticketRepository.delete(ticket);
    }

    @Override
    public void cancelEntryPass(TicketModel ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket must not be null");
        }

       TicketEntity ticketEntity = ticketRepository.findById(UUID.fromString(ticket.getId()))
               .orElseThrow(
                       () -> new ResourceNotFoundException("Ticket", "TicketId", ticket.getId())
               );
        ShowEntity showEntity = showRepository.findById(UUID.fromString(ticketEntity.getShowId()))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Show", "ShowId", ticketEntity.getShowId())
                );

        showEntity.setBookings(showEntity.getBookings() - ticketEntity.getAllowedPersons());
        showEntity.setReserved(showEntity.getReserved() - ticketEntity.getAllowedPersons());
        ticketRepository.delete(ticketEntity);
        showRepository.save(showEntity);
    }


    private boolean reserveSeatsInRedis(UUID showId, UUID userId, List<UUID> showSeatIds) {
        for (UUID seatId : showSeatIds) {
            String key = "show:" + showId + ":seat:" + seatId;
            String value = "reserved" + userId;

            Boolean reserved =
                    redisTemplate.opsForValue().setIfAbsent(key, value);
            // rollback previous reservations if any fails
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
        UserEntity user = this.getUserContext();
        TicketEntity ticket = new TicketEntity();
        ticket.setShowName(showEntity.getShowName());
        ticket.setDate(Date.from(showEntity.getShowDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        ticket.setStartTime(showEntity.getShowTime());
        ticket.setUser(user);
        ticket.setHostId(showEntity.getUser().getId().toString());
        ticket.setShowId(showEntity.getShowId().toString());

        List<ShowSeatEntity> showSeats =
                showSeatRepository.findByShowShowId(showEntity.getShowId())
                        .stream()
                        .filter(seat -> showSeatIds.contains(seat.getId()))
                        .peek(seat -> seat.setSeatStatus(SeatStatus.BOOKED))
                        .toList();

        String hostId = showEntity.getUser().getId().toString();


        ticketRepository.save(ticket);

        String token = encryptionUtil.encrypt(ticket.getId().toString());

        try {
            MultipartFile qrCode = qrCodeService.generateQRCode(token, ticket.getId().toString());
            ticket.setTicketQRCode(bucketService.uploadFile(qrCode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        showSeats.forEach(ticket::addShowSeat);
        user.addTicket(ticket);
        userRepository.save(user);
        return ticketRepository.save(ticket);
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
        System.out.println("Principal: " + principal);
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Failed to fetch user: " + username));

    }

}
