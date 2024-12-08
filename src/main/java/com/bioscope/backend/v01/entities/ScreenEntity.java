package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.constants.ArrangementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "screens")
@Getter
@Setter
public class ScreenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID screenId;

    private String screenName;

    @ManyToOne
    @JoinColumn(name = "event_host_id")
    private EventHostEntity eventHost;


    @OneToOne
    @JoinColumn(name = "seating_arrangement")
    private SeatingArrangementEntity seatingArrangement;


    @OneToMany(mappedBy = "screen")
    private List<ShowEntity> shows;

    private int totalAvailableSeats = findTotalSeats();

    private int findTotalSeats() {
        if (seatingArrangement != null) {
            if(Objects.equals(seatingArrangement.getArrangementType(), ArrangementType.STANDING)) {
                return new AtomicInteger(seatingArrangement.getCapacity() - seatingArrangement.getBookedSeats()).get();
            }
            List<SeatRowEntity> rows = seatingArrangement.getSeatRow();
            if(rows != null) {
                AtomicInteger totalSeats = new AtomicInteger();
                for (SeatRowEntity row : rows) {
                    List<SeatEntity> seats = row.getSeats();
                    if(seats != null) {
                        seats.forEach(seat -> {
                            if (seat.getBookingStatus() == 0) {
                                totalSeats.getAndIncrement();
                            }
                        });
                    }
                }
                return totalSeats.get();
            }
        }
        return 0;
    }

}
