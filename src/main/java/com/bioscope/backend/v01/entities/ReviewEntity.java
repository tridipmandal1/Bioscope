package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewEntity {

    @Id
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
    private UserEntity user;
}
