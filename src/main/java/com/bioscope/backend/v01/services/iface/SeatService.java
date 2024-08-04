package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.SeatDto;

public interface SeatService {

    SeatDto getSeat(long seatNumber);
    SeatDto bookSeat(long seatNumber);
    String getSeatPrice(long seatNumber);
    SeatDto cancelSeatBooking(long seatNumber);
    SeatDto addSeat(SeatDto seatDto);
    SeatDto updateSeat(SeatDto seatDto);
    void deleteSeat(long seatNumber);

}
