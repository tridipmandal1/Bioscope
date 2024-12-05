package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class SeatRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID rowId;

    private Integer rowNumber;

    @OneToMany(mappedBy = "seatRowEntity")
    private List<SeatEntity> seats;

    @ManyToOne
    @JoinColumn(name = "arrangement_id")
    private SeatingArrangementEntity seatingArrangement;
}
