package com.bioscope.backend.v01.models.host;

import lombok.Data;

@Data
public class SeatModel {
    private String id;
    private Integer seatNumber;
    private int bookingStatus;
    private String category;
}
