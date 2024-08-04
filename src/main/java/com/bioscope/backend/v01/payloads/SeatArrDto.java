package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.ScreenEntity;
import com.bioscope.backend.v01.entities.SeatEntity;
import com.bioscope.backend.v01.entities.SeatRowEntity;

import java.util.HashSet;
import java.util.List;

public class SeatArrDto {

    private HashSet<SeatEntity> seats;

    private List<SeatRowEntity> seatArrangement;

    private ScreenEntity screen;
}
