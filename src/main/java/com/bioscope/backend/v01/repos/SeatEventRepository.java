package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.SeatEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatEventRepository extends JpaRepository<SeatEventEntity, Long> {
}