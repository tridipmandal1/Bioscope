package com.bioscope.backend.v01.services.impl;


import com.bioscope.backend.v01.entities.SeatEventEntity;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.repos.SeatEventRepository;
import com.bioscope.backend.v01.repos.ShowRepository;
import com.bioscope.backend.v01.repos.ShowSeatRepository;
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


    public ReservationCleanupService(
            RedisTemplate<String, String> redisTemplate,
            SeatEventRepository seatEventRepository,
            ShowRepository showRepository) {
        this.redisTemplate = redisTemplate;
        this.seatEventRepository = seatEventRepository;
        this.showRepository = showRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(ReservationCleanupService.class);

    @Scheduled(fixedRate = 60000) // Run every minute
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
            if (ttl != null && ttl <= 0) { // Expired
                // Validate key format
                String[] parts = key.split(":");
                if (parts.length != 4 || !parts[0].equals("show") || !parts[2].equals("seat")) {
                    logger.warn("Invalid key format detected: {}", key);
                    redisTemplate.delete(key); // Clean up invalid keys
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
            if (parts.length == 3 && parts[0].equals("show") && parts[2].equals("passes")) {
                UUID showId = UUID.fromString(parts[1]);
                String reservedStr = redisTemplate.opsForValue().get(key);
                int reserved = reservedStr != null ? Integer.parseInt(reservedStr) : 0;
                redisTemplate.delete(key);
                showRepository.findById(showId).ifPresent(show -> {
                    show.setReserved(0); // Reset or adjust based on your logic
                    showRepository.save(show);
                });
                seatEventRepository.save(new SeatEventEntity("PassExpired", showId, reserved, null));
            }
        }
    }

}
