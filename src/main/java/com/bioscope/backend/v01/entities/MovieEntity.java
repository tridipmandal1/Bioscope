package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Setter
@Getter
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID movieId;


    private String title;

    private String description;

    private String language;

    @Lob
    private byte[] poster;

    private Float rating = calculateRating();

    private String duration;

    private String releaseDate;

    private String trailerUrl;

    private String casts;


    @OneToMany(mappedBy = "movie")
    private List<ReviewEntity> reviews;

    @ManyToOne
    private GenreEntity genre;

    private  boolean isCurrentlyStreaming;

    @ManyToMany(mappedBy = "watchedMovies")
    private List<UserEntity> users;

    @OneToMany
    private Set<ShowEntity> show;

    private Float calculateRating() {
        AtomicReference<Double> total = new AtomicReference<>(0.0);
        if (reviews != null) {
            reviews.forEach(review -> total.updateAndGet(v -> v + review.getRating()));
            return (float) (total.get() / reviews.size());
        }

         return 0F;
    }

}
