package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, UUID> {

    @Query(value = "SELECT m FROM MovieEntity m WHERE m.isCurrentlyStreaming")
    List<MovieEntity> getCurrentlyStreamingMovies();

    @Query("SELECT m FROM MovieEntity m WHERE m.isCurrentlyStreaming = false ORDER BY m.views DESC")
    List<MovieEntity> trendingMovies();

    @Query(
            "SELECT DISTINCT m FROM MovieEntity m " +
                    "LEFT JOIN m.genre g " +
                    "WHERE m.isCurrentlyStreaming = false " +
                    "AND (" +
                    "g.genreName LIKE CONCAT('%',:query, '%') " +
                    "OR m.title LIKE CONCAT('%', :query, '%') " +
                    "OR m.casts LIKE CONCAT('%', :query, '%') " +
                    "OR m.language LIKE CONCAT('%', :query, '%')" +
                    ")"
    )
    List<MovieEntity> searchMovies(@Param("query") String query);

}

