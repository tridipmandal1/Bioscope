package com.bioscope.backend.v01.models;

import lombok.Data;

import java.util.List;

@Data
public class GenreModel {
    private String genreId;
    private String genreName;
    private List<String> movies;

}
