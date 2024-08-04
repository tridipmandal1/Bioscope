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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID show_id;

    @OneToOne
    private EventHostEntity eventHost;

    private String showName;

    @OneToOne
    private MovieEntity movie;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime showTime;

    @OneToOne
    private SeatingArrangementEntity seatingArrangement;

    @OneToOne
    private ScreenEntity screen;


}
