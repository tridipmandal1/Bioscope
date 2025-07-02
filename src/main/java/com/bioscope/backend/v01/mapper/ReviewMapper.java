package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.ReviewEntity;
import com.bioscope.backend.v01.models.ReviewModel;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {


    public ReviewModel entityToModel (ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        ReviewModel model = new ReviewModel();
        model.setReviewId(entity.getReviewId().toString());
        model.setMovieId(entity.getMovie().getMovieId().toString());
        model.setUsername(entity.getUser().getId().toString());
        model.setReview(entity.getReview());
        model.setRating(entity.getRating().toString());
        model.setDate(entity.getReviewDate().toString());
        return model;
    }

    public ReviewEntity modelToEntity(ReviewModel model) {
        if (model == null) {
            return null;
        }
        ReviewEntity entity = new ReviewEntity();
        entity.setReview(model.getReview());
        entity.setRating(Double.parseDouble(model.getRating()));
        return entity;
    }
}
