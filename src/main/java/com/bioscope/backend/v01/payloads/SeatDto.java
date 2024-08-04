package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.enums.SeatCategory;
import lombok.Data;

@Data
public class SeatDto {

    private long seatNumber;

    private SeatCategory seatCategory;

    private boolean isBooked;

}
