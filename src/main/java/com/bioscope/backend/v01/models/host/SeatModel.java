package com.bioscope.backend.v01.models.host;

import com.bioscope.backend.v01.constants.SeatId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatModel {
    private SeatId id;
    private Integer seatNumber;
    private Integer price;
}
