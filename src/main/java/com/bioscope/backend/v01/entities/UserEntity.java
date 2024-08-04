package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID userID;

    @Email
    private String email;

    private String password;


    private String firstName;

    private String lastName;


    private String phoneNumber;

    private String hometown;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<GenreEntity> interests;

    @OneToMany
    private List<MovieEntity> watchedMovies;
}
