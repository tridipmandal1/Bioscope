package com.bioscope.backend.v01.entities;


import com.bioscope.backend.v01.constants.SeatId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatEntity {

    @EmbeddedId
    private SeatId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id", referencedColumnName = "rowId")
    private SeatRowEntity seatRowEntity;

    private Integer price;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeatEntity> showSeats = new ArrayList<>();


    public void addShowSeat(ShowSeatEntity showSeat) {
        showSeats.add(showSeat);
        showSeat.setSeat(this);
    }
}
