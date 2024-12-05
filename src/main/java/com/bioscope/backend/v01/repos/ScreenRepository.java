package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.ScreenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScreenRepository extends JpaRepository<ScreenEntity, UUID> {

}
