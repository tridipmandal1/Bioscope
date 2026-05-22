package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharUUIDJdbcType;

import java.util.UUID;

@Entity
@Table(name = "show_seats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShowSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", referencedColumnName = "showId", nullable = false)
    private ShowEntity show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "seat_id_seating_arrangement_id", referencedColumnName = "seatingArrangementId"),
            @JoinColumn(name = "seat_id_row_index", referencedColumnName = "rowIndex"),
            @JoinColumn(name = "seat_id_seat_number", referencedColumnName = "seatNumber")
    })
    private SeatEntity seat;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private TicketEntity ticket;
}