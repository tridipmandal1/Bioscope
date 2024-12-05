package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SeatRepository extends JpaRepository<SeatEntity, UUID> {
}
