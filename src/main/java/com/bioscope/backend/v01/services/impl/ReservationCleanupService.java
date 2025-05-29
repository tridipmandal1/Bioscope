package com.bioscope.backend.v01.services.impl;


import com.bioscope.backend.v01.entities.PassCategoryEntity;
import com.bioscope.backend.v01.entities.SeatEventEntity;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.repos.SeatEventRepository;
import com.bioscope.backend.v01.repos.ShowRepository;
import com.bioscope.backend.v01.repos.ShowSeatRepository;
import com.bioscope.backend.v01.repos.TicketRepository;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationCleanupService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SeatEventRepository seatEventRepository;
    private final ShowRepository showRepository;
    private final TicketRepository ticketRepository;


    public ReservationCleanupService(
            RedisTemplate<String, String> redisTemplate,
            SeatEventRepository seatEventRepository,
            ShowRepository showRepository,
            TicketRepository ticketRepository) {
        this.redisTemplate = redisTemplate;
        this.seatEventRepository = seatEventRepository;
        this.showRepository = showRepository;
        this.ticketRepository = ticketRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(ReservationCleanupService.class);

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredReservations() {
        Set<String> seatKeys = redisTemplate.keys("show:*:seat:*");
        if (seatKeys != null) {
            seatKeys.forEach(this::cleanupSeatKey);
        }

        Set<String> passKeys = redisTemplate.keys("show:*:passes");
        if (passKeys != null) {
            passKeys.forEach(this::cleanupPassKey);
        }
        logger.info("Expired reservations cleaned up.");
    }

    private void cleanupSeatKey(String key) {
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl <= 0) {
                String[] parts = key.split(":");
                if (parts.length != 4 || !parts[0].equals("show") || !parts[2].equals("seat")) {
                    logger.warn("Invalid key format detected: {}", key);
                    redisTemplate.delete(key);
                    return;
                }

                UUID showId = UUID.fromString(parts[1]);
                UUID seatId = UUID.fromString(parts[3]);
                String value = redisTemplate.opsForValue().get(key);

                if (value != null && value.startsWith("reserved:")) {
                    String[] valueParts = value.split(":");
                    if (valueParts.length < 2) {
                        logger.warn("Invalid value format for key {}: {}", key, value);
                        redisTemplate.delete(key);
                        return;
                    }
                    String userId = valueParts[1];
                    redisTemplate.delete(key);
                    seatEventRepository.save(new SeatEventEntity("ReservationExpired", showId, seatId, UUID.fromString(userId)));

                    ticketRepository.findByShowIdAndPaymentStatus(showId.toString(), "PENDING").forEach(ticket -> {
                        if (ticket.getShowSeats().stream().anyMatch(seat -> seat.getId().equals(seatId))) {
                            ticket.setPaymentStatus("FAILED");
                            ticketRepository.save(ticket);
                        }
                    });
                    logger.debug("Expired reservation cleaned up for key: {}", key);
                } else {
                    logger.debug("Skipping non-reserved key: {}", key);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing key {}: {}", key, e.getMessage());
        }
    }

    private void cleanupPassKey(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl <= 0) {
            String[] parts = key.split(":");
            if (parts.length == 5 && parts[0].equals("show") && parts[2].equals("category") && parts[4].equals("passes")) {
                UUID showId = UUID.fromString(parts[1]);
                String reservedStr = redisTemplate.opsForValue().get(key);
                int reserved = reservedStr != null ? Integer.parseInt(reservedStr) : 0;
                redisTemplate.delete(key);
                showRepository.findById(showId).ifPresent(show -> {
                    PassCategoryEntity passCategory =
                            show.getTicketPrice()
                                    .stream()
                                    .filter(cat -> cat
                                            .getCategory()
                                            .equals(parts[3].toUpperCase())).findFirst().orElseThrow(
                                            () -> new ResourceNotFoundException("PassCategory", "Category", parts[3].toUpperCase())
                                    );
                    show.setReserved(0);
                    showRepository.save(show);
                });
                seatEventRepository.save(new SeatEventEntity("PassExpired", showId, reserved, null));

                ticketRepository.findByShowIdAndPaymentStatus(showId.toString(), "PENDING").forEach(ticket -> {
                    ticket.setPaymentStatus("FAILED");
                    ticketRepository.save(ticket);
                });
            }
        }
    }

}
