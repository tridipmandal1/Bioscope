package com.bioscope.backend.v01.models;

import lombok.Data;

import java.util.List;

@Data
public class MovieModel {

    private String movieId;
    private String title;
    private String description;
    private String genre;
    private String duration;
    private String rating;
    private String language;
    private String releaseDate;
    private String trailerUrl;
    private String casts;
    private List<ReviewModel> reviews;
    private boolean isCurrentlyStreaming;
}
