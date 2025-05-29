package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.PassCategoryEntity;
import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.repos.PassCategoryRepository;
import com.bioscope.backend.v01.repos.ShowRepository;
import com.bioscope.backend.v01.repos.ShowSeatRepository;
import com.bioscope.backend.v01.repos.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.entities.TicketEntity;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JpaRedisSync {

    private final ShowSeatRepository showSeatRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ShowRepository showRepository;
    private final TicketRepository ticketRepository;
    private final PassCategoryRepository passCategoryRepository;

    public JpaRedisSync(ShowSeatRepository showSeatRepository,
                        RedisTemplate<String, String> redisTemplate,
                        ShowRepository showRepository,
                        TicketRepository ticketRepository, PassCategoryRepository passCategoryRepository) {
        this.showSeatRepository = showSeatRepository;
        this.redisTemplate = redisTemplate;
        this.showRepository = showRepository;
        this.ticketRepository = ticketRepository;
        this.passCategoryRepository = passCategoryRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
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
                    if (parts.length != 5 || !parts[0].equals("show") || !parts[2].equals("category") || !parts[4].equals("passes")) {
                        log.warn("Invalid pass key format: {}", key);
                        return;
                    }

                    UUID showId = UUID.fromString(parts[1]);
                    String reservedStr = redisTemplate.opsForValue().get(key);
                    int reservedInRedis = reservedStr != null ? Integer.parseInt(reservedStr) : 0;

                    showRepository.findById(showId).ifPresent(show -> {
                        if (show.getArrangementType() == ArrangementType.STANDING) {
                            int confirmedBookings = ticketRepository.findByShowIdAndPaymentStatus(showId.toString(), "SUCCESS")
                                    .stream()
                                    .mapToInt(TicketEntity::getAllowedPersons)
                                    .sum();
                            if (reservedInRedis != confirmedBookings) {
                                PassCategoryEntity passCategory =
                                        show.getTicketPrice()
                                                .stream()
                                                .filter(cat -> cat
                                                        .getCategory()
                                                        .equals(parts[3].toUpperCase()))
                                                .findFirst().orElseThrow(
                                                        () -> new ResourceNotFoundException("PassCategory", "Category", parts[3].toUpperCase())
                                                );
                                passCategory.setReserved(confirmedBookings);
                                passCategoryRepository.save(passCategory);
                                show.setBookings(confirmedBookings);
                                showRepository.save(show);
                                log.debug("Synced reserved passes for show {}: {} passes", showId, confirmedBookings);
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
                            boolean isConfirmed = ticketRepository.findByShowIdAndPaymentStatus(showId.toString(), "SUCCESS")
                                    .stream()
                                    .flatMap(t -> t.getShowSeats().stream())
                                    .anyMatch(s -> s.getId().equals(seatId));
                            if (isConfirmed && seat.getSeatStatus() != SeatStatus.BOOKED) {
                                seat.setSeatStatus(SeatStatus.BOOKED);
                                showSeatRepository.save(seat);
                                log.debug("Synced seat {} to BOOKED for show {}", seatId, showId);
                            }
                        });
                    }
                } catch (Exception e) {
                    log.error("Error syncing seat key {}: {}", key, e.getMessage());
                }
            });
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeRedis() {
        ticketRepository.findByPaymentStatus("SUCCESS").forEach(ticket -> {
            ShowEntity show = showRepository.findById(UUID.fromString(ticket.getShowId())).orElse(null);
            if (show != null) {
                if (show.getArrangementType() == ArrangementType.STANDING) {
                    String key = "show:" + show.getShowId() + "category:" + ticket.getCategory().toUpperCase()  + ":passes";
                    int confirmedBookings = ticketRepository.findByShowIdAndPaymentStatus(show.getShowId().toString(), "SUCCESS")
                            .stream()
                            .mapToInt(TicketEntity::getAllowedPersons)
                            .sum();
                    redisTemplate.opsForValue().set(key, String.valueOf(confirmedBookings));
                } else {
                    ticket.getShowSeats().forEach(seat -> {
                        String key = "show:" + show.getShowId() + ":seat:" + seat.getId();
                        redisTemplate.opsForValue().set(key, "booked");
                    });
                }
            }
        });
    }
}
