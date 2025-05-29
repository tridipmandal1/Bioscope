package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID movieId;


    private String title;

    private String description;

    private String language;

    private String poster;

    private Float rating;

    private String duration;

    private String releaseDate;

    private String trailerUrl;

    private String casts;


    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ReviewEntity> reviews;

    private Integer views;

    @ManyToMany
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<GenreEntity> genre;

    private  boolean isCurrentlyStreaming = true;

    @ManyToMany(mappedBy = "watchedMovies")
    private List<UserEntity> users;

    @OneToMany(mappedBy = "movie")
    private List<ShowEntity> show;

    public Float calculateRating() {
        Double total = 0.0;
        if (reviews != null) {
            for (ReviewEntity review : reviews) {
                total += review.getRating();
            }

            return (float) (total / reviews.size());
        }

         return 0F;
    }

}
