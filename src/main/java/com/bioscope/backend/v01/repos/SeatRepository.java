package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.constants.SeatId;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.entities.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, SeatId> {

    List<SeatEntity> findByIdSeatingArrangementIdAndIdRowIndex(UUID seatingArrangementId, String rowIndex);
    Optional<SeatEntity> findByIdSeatingArrangementIdAndIdRowIndexAndIdSeatNumber(UUID seatingArrangementId, String rowIndex, Integer seatNumber);
    List<SeatEntity> findBySeatRowEntityRowId(UUID rowId);
}
