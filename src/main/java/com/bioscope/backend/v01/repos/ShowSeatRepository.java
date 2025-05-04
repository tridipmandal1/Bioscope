package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.constants.SeatId;
import com.bioscope.backend.v01.entities.ShowSeatEntity;
import com.bioscope.backend.v01.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeatEntity, UUID> {
    List<ShowSeatEntity> findByShowShowId(UUID showId);
    List<ShowSeatEntity> findByShowShowIdAndSeatStatus(UUID showId, SeatStatus seatStatus);
    Optional<ShowSeatEntity> findByShowShowIdAndSeatId(UUID showId, SeatId seatId);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM ShowSeatEntity s WHERE s.id=:seatId AND s.seatStatus = 'AVAILABLE'")
    boolean isSeatShowAvailable(@Param("seatId") UUID seatId);

    @Modifying
    @Query("UPDATE ShowSeatEntity s SET s.seatStatus = 'AVAILABLE' WHERE s.id=:seatId")
    void cancelSeatReservation(UUID seatId);
}