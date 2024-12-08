package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SeatArrangementRepository extends JpaRepository<SeatingArrangementEntity, UUID> {
}
