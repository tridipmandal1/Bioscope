package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.PassCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassCategoryRepository extends JpaRepository<PassCategoryEntity, UUID> {
}
