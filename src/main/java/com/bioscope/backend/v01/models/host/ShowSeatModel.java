package com.bioscope.backend.v01.models.host;

import com.bioscope.backend.v01.constants.SeatId;
import lombok.Data;

@Data
public class ShowSeatModel {
    private String sSId;
    private SeatId seatId;
    private String seatStatus;
}
