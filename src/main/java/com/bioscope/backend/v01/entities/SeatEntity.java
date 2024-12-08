package com.bioscope.backend.v01.entities;
import com.bioscope.backend.v01.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatCategory seatCategory;
    /**
     * -1 - Not-available
     * 0 - Not-booked
     * 1 - Booked
     */
    private int bookingStatus = -1;

    @ManyToOne
    @JoinColumn(name = "row_id")
    private SeatRowEntity seatRowEntity;

}
