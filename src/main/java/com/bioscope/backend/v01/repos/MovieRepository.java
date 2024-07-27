package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<MovieEntity, UUID>, JpaSpecificationExecutor<MovieEntity> {
}
