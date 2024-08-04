package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SeatArrRepository extends JpaRepository<SeatingArrangementEntity, UUID>, JpaSpecificationExecutor<SeatingArrangementEntity> {
}
