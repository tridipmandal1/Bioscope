package com.bioscope.backend.v01.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Entity
@Getter
@Setter
public class SeatRowEntity {

    @Id
    private Long rowId;

    @OneToMany
    private List<SeatEntity> seats;

    @ManyToOne
    private SeatingArrangementEntity seatingArrangement;
}
