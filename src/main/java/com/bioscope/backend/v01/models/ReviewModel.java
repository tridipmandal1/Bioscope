package com.bioscope.backend.v01.models;

import lombok.Data;

@Data
public class ReviewModel {

    private String reviewId;
    private String movieId;
    private String username;
    private String review;
    private String rating;
    private String date;
}
