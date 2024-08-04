package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.EventHostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EventHostRepository extends JpaRepository<EventHostEntity, UUID>, JpaSpecificationExecutor<EventHostEntity> {
}
