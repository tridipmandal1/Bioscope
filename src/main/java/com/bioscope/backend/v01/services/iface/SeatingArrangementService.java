package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.SeatArrDto;
import java.util.UUID;

public interface SeatingArrangementService {

    SeatArrDto getSeatingArrangement(UUID arrangement_id);
    SeatArrDto createSeatingArrangement(SeatArrDto seatArrDto);
    SeatArrDto updateSeatingArrangement(SeatArrDto seatArrDto);
    void deleteSeatingArrangement(UUID arrangement_id);
    void bookSeat(UUID arrangement_id, Long seat_id,UUID user_id);
}
