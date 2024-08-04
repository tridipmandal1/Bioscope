package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.GenreEntity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class MovieDto {

    private String title;

    private String description;

    private String language;

    private String duration;

    private String releaseDate;

    private String trailerUrl;

    private String casts;

    private String director;

    private  boolean isCurrentlyStreaming;

    private GenreEntity genre;
}
