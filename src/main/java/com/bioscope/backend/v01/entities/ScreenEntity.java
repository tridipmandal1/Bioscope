package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "screens")
@Getter
@Setter
public class ScreenEntity {

    @Id
    @Column(name = "screen_id")
    private UUID screenId;

    @Column(name = "screen_name")
    private String screenName;

    @OneToOne
    @JoinColumn(name = "seating_arrangement")
    private SeatingArrangementEntity seatingArrangement;

    @OneToOne
    private EventHostEntity eventHost;

    @OneToOne
    private ShowEntity show;

}
