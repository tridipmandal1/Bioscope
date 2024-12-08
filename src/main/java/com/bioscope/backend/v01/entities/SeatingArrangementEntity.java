package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class SeatingArrangementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID arrangement_id;

    private String arrangementType;

    @OneToMany(mappedBy = "seatingArrangement")
    private List<SeatRowEntity> seatRow;

    @OneToOne(mappedBy = "seatingArrangement")
    private ScreenEntity screen;


    private Integer capacity;

    private Integer bookedSeats;

    private Double price;

}
