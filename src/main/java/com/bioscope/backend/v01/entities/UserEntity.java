package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userID;


    private String email;

    @Valid
    private String password;


    private String name;


    private String currentLocation;

    @ManyToMany
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<GenreEntity> interests;

    @ManyToMany
    @JoinTable(
            name = "watched_movies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<MovieEntity> watchedMovies;

    @OneToMany(mappedBy = "user")
    private List<ReviewEntity> reviews;
}
