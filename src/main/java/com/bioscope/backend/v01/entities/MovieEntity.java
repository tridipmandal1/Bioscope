package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID movieId = UUID.randomUUID();

    private String title;

    private String description;

    private String language;

    private String duration;

    private String releaseDate;

    private String trailerUrl;

    private String casts;

    private String director;

    @ManyToOne
    private GenreEntity genre;

    private  boolean isCurrentlyStreaming;

}
