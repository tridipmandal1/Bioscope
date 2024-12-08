package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.SeatRowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeatRowRepository extends JpaRepository<SeatRowEntity, UUID> {

}
