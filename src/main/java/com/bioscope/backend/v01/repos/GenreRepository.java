package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GenreRepository extends JpaRepository<GenreEntity,Long>, JpaSpecificationExecutor<GenreEntity> {
}
