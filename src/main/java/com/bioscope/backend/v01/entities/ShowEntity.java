package com.bioscope.backend.v01.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID show_id;

    @OneToOne
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    private String showType;

    private String showDescription;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String showDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime showTime;

    @JsonFormat(pattern = "HH:mm")
    private String showDuration;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    private ScreenEntity screen;

}
