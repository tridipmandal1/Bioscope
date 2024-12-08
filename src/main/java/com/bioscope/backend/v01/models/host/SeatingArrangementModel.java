package com.bioscope.backend.v01.models.host;

import lombok.Data;
import java.util.List;

@Data
public class SeatingArrangementModel {

        private String arrangementId;
        private String arrangementType;
        private List<SeatRowModel> seatRow;
        private Integer capacity;
        private Integer bookedSeats;
        private Double price;
}
