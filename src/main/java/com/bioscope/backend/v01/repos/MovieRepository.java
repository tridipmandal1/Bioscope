package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<MovieEntity, UUID>,
        JpaSpecificationExecutor<MovieEntity> {

    @Query(value = "SELECT * FROM movies WHERE title = :title",nativeQuery = true)
    MovieEntity getMovieByTitle(String title);

    @Query(value = "SELECT * FROM movies WHERE genreId= :genreId",nativeQuery = true)
    List<MovieEntity> getMoviesByGenreId(long genreId);

    @Query(value = "SELECT * FROM movies WHERE genreName= :genreName",nativeQuery = true)
    List<MovieEntity> getMoviesByGenreName(String genreName);
}
