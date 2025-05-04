package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, UUID> {

    Optional<GenreEntity> findByGenreName(String genreName);

    boolean existsByGenreName(String genreName);
    Optional<GenreEntity> findGenreEntitiesByGenreName(String genreName);
}
