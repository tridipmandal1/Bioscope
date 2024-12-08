package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.ReviewEntity;
import com.bioscope.backend.v01.models.ReviewModel;
import com.bioscope.backend.v01.repos.MovieRepository;
import com.bioscope.backend.v01.repos.ReviewRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class ReviewMapper {


    /**
     * Maps a ReviewEntity to a ReviewModel
     * @param entity {@link ReviewEntity}
     * @return {@link ReviewModel}
     */
    ReviewModel entityToModel (ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        ReviewModel model = new ReviewModel();
        model.setReviewId(entity.getReviewId().toString());
        model.setMovieId(entity.getMovie().getMovieId().toString());
        model.setUsername(entity.getUser().getUserID().toString());
        model.setReview(entity.getReview());
        model.setRating(entity.getRating().toString());
        model.setDate(entity.getReviewDate().toString());
        return model;
    }

    /**
     * Maps a ReviewModel to a ReviewEntity
     * @param model {@link ReviewModel}
     * @return {@link ReviewEntity}
     */
    ReviewEntity modelToEntity (ReviewModel model) {
        if (model == null) {
            return null;
        }
        ReviewEntity entity = new ReviewEntity();
        entity.setReview(model.getReview());
        entity.setRating(Double.parseDouble(model.getRating()));
        entity.setReviewDate(new Date());
        return entity;
    }
}
