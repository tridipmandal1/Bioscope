package com.bioscope.backend.v01.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharUUIDJdbcType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "genres")
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID genreId;


    private String genreName;


    @ManyToMany(mappedBy = "interests", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<UserEntity>  users;

    @ManyToMany(mappedBy = "genre")
    private List<MovieEntity> movies;

    public void addMovie(MovieEntity movie) {
        movies.add(movie);
    }

}
