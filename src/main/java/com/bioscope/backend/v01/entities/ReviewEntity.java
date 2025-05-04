package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID reviewId;

    private String review;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewDate;

    private Double rating;

    @ManyToOne
    @JoinColumn(
            name = "movie_id",
            referencedColumnName = "movieId"
    )
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private UserEntity user;
}
