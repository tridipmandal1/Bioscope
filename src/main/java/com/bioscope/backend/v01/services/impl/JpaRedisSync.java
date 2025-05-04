package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.repos.ShowRepository;
import com.bioscope.backend.v01.repos.ShowSeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JpaRedisSync {

    private final ShowSeatRepository showSeatRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ShowRepository showRepository;

    public JpaRedisSync(ShowSeatRepository showSeatRepository,
                        RedisTemplate<String, String> redisTemplate,
                        ShowRepository showRepository) {
        this.showSeatRepository = showSeatRepository;
        this.redisTemplate = redisTemplate;
        this.showRepository = showRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional// Every 5 minutes
    public void syncRedisToJpa() {
        log.info("Starting Redis to JPA sync...");

        syncSeatedShows();

        syncStandingShows();

        log.info("Redis to JPA sync completed.");
    }

    private void syncStandingShows() {
        Set<String> passKeys = redisTemplate.keys("show:*:passes");
        if (passKeys != null && !passKeys.isEmpty()) {
            passKeys.forEach(key -> {
                try {
                    String[] parts = key.split(":");
                    if (parts.length != 3 || !parts[0].equals("show") || !parts[2].equals("passes")) {
                        log.warn("Invalid pass key format: {}", key);
                        return;
                    }

                    UUID showId = UUID.fromString(parts[1]);
                    String reservedStr = redisTemplate.opsForValue().get(key);
                    int reservedInRedis = reservedStr != null ? Integer.parseInt(reservedStr) : 0;

                    showRepository.findById(showId).ifPresent(show -> {
                        if (show.getArrangementType() == ArrangementType.STANDING) {
                            int currentReserved = show.getReserved() != null ? show.getReserved() : 0;
                            if (reservedInRedis != currentReserved) {
                                show.setReserved(reservedInRedis);
                                show.setBookings(reservedInRedis); // Assuming bookings = reserved for simplicity
                                showRepository.save(show);
                                log.debug("Synced reserved passes for show {}: {} passes", showId, reservedInRedis);
                            }
                        }
                    });
                } catch (Exception e) {
                    log.error("Error syncing pass key {}: {}", key, e.getMessage());
                }
            });
        }

    }

    private void syncSeatedShows() {
        Set<String> seatKeys = redisTemplate.keys("show:*:seat:*");
        if (seatKeys != null && !seatKeys.isEmpty()) {
            seatKeys.forEach(key -> {
                try {
                    String[] parts = key.split(":");
                    if (parts.length != 4 || !parts[0].equals("show") || !parts[2].equals("seat")) {
                        log.warn("Invalid seat key format: {}", key);
                        return;
                    }

                    UUID showId = UUID.fromString(parts[1]);
                    UUID seatId = UUID.fromString(parts[3]);
                    String status = redisTemplate.opsForValue().get(key);

                    if ("booked".equals(status)) {
                        showSeatRepository.findById(seatId).ifPresent(seat -> {
                            if (seat.getSeatStatus() != SeatStatus.BOOKED) {
                                seat.setSeatStatus(SeatStatus.BOOKED);
                                showSeatRepository.save(seat);
                                log.debug("Synced seat {} to BOOKED for show {}", seatId, showId);
                            }
                        });
                    } else if ("reserved".equals(status) && redisTemplate.getExpire(key, TimeUnit.SECONDS) <= 0) {
                        // Handle expired reservations missed by cleanup
                        redisTemplate.delete(key);
                        log.debug("Removed expired reservation for seat {} in show {}", seatId, showId);
                    }
                } catch (Exception e) {
                    log.error("Error syncing seat key {}: {}", key, e.getMessage());
                }
            });
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeRedis() {
        showSeatRepository.findAll().forEach(seat -> {
            String key = "show:" + seat.getShow().getShowId() + ":seat:" + seat.getId();
            if (seat.getSeatStatus() == SeatStatus.BOOKED) {
                redisTemplate.opsForValue().set(key, "booked");
            }
        });
        showRepository.findAll().forEach(show -> {
            if (show.getArrangementType() == ArrangementType.STANDING && show.getReserved() != null) {
                String key = "show:" + show.getShowId() + ":passes";
                redisTemplate.opsForValue().set(key, String.valueOf(show.getReserved()));
            }
        });
    }
}
