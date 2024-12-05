package com.bioscope.backend.v01.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "genres")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID genreId;


    private String genreName;


    @ManyToMany(mappedBy = "interests", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<UserEntity>  users;

    @OneToMany(mappedBy = "genre")
    private List<MovieEntity> movies;

}
