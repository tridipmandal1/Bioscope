package com.bioscope.backend.v01.entities;
import com.bioscope.backend.v01.enums.SeatCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
public class SeatEntity {

    @Id
    private long seatNumber;

    private SeatCategory seatCategory;

    private boolean isBooked;

    @ManyToOne
    private SeatRowEntity seatRowEntity;



}
