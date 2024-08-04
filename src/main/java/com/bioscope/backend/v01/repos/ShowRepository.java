package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.ShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ShowRepository extends JpaRepository<ShowEntity, UUID>, JpaSpecificationExecutor<ShowEntity> {
}
