package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.MovieEntity;
import lombok.Data;

import java.util.HashSet;

@Data
public class GenreDto {
    private long genreId;
    private String genreName;
    private HashSet<MovieEntity> movies;

}
