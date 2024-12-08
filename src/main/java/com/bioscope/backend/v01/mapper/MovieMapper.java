package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.models.MovieModel;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    private final ReviewMapper reviewMapper;

    public MovieMapper(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    /**
     * Maps the movie entity to movie model
     * @param movieEntity {@link MovieEntity}
     * @return {@link MovieModel}
     */
    MovieModel entityToModel (MovieEntity movieEntity) {
        if (movieEntity == null) {
            return null;
        }
        MovieModel movieModel = new MovieModel();
        movieModel.setMovieId(movieEntity.getMovieId().toString());
        movieModel.setTitle(movieEntity.getTitle());
        movieModel.setDescription(movieEntity.getDescription());
        movieModel.setGenre(movieEntity.getGenre().getGenreName());
        movieModel.setDuration(movieEntity.getDuration());
        movieModel.setRating(String.valueOf(movieEntity.getRating()));
        movieModel.setLanguage(movieEntity.getLanguage());
        movieModel.setReleaseDate(movieEntity.getReleaseDate());
        movieModel.setTrailerUrl(movieEntity.getTrailerUrl());
        movieModel.setCasts(movieEntity.getCasts());
        if (movieEntity.getReviews() != null) {
            movieModel.setReviews(
                    movieEntity.getReviews().stream()
                            .map(reviewMapper :: entityToModel).collect(Collectors.toList())
            );
        }
        movieModel.setCurrentlyStreaming(movieEntity.isCurrentlyStreaming());
        return movieModel;
    }

    /**
     * Maps the movie model to movie entity
     * @param movieModel {@link MovieModel}
     * @return {@link MovieEntity}
     */
    MovieEntity modelToEntity (MovieModel movieModel) {
        if (movieModel == null) {
            return null;
        }
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(movieModel.getTitle());
        movieEntity.setDescription(movieModel.getDescription());
        movieEntity.setDuration(movieModel.getDuration());
        movieEntity.setLanguage(movieModel.getLanguage());
        movieEntity.setReleaseDate(movieModel.getReleaseDate());
        movieEntity.setTrailerUrl(movieModel.getTrailerUrl());
        movieEntity.setCasts(movieModel.getCasts());
        if (!movieModel.getReviews().isEmpty()){
            movieEntity.setReviews(
                    movieModel.getReviews().stream()
                            .map(reviewMapper :: modelToEntity).collect(Collectors.toList())
            );
        }
        movieEntity.setCurrentlyStreaming(movieModel.isCurrentlyStreaming());
        return movieEntity;
    }
}
