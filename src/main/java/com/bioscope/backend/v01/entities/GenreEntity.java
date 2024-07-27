package com.bioscope.backend.v01.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Entity
@Getter
@Setter

public class GenreEntity {

    @Id
    private long interestId;

    private String interestName;

    @ManyToOne
    private UserEntity user;

    @OneToMany
    private HashSet<MovieEntity> movies;

}
