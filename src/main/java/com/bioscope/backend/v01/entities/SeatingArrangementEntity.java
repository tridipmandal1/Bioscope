package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Getter
@Setter
public class SeatingArrangementEntity {

    @Id
    private UUID arrangement_id;

    @OneToMany
    private HashSet<SeatEntity> seats;

    @OneToMany
    private List<SeatRowEntity> seatArrangement;

    @OneToOne
    private ScreenEntity screen;



}
