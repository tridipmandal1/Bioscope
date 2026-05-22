package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharUUIDJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat_events")
public class SeatEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventType;

    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")// "SeatReserved", "SeatBooked", "ReservationExpired"
    private UUID showId;
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID showSeatId;
    private Integer quantity;
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID userId;
    @CreationTimestamp
    private LocalDateTime timestamp;

    public SeatEventEntity(String eventType, UUID showId, UUID showSeatId, UUID userId) {
        this.eventType = eventType;
        this.showId = showId;
        this.showSeatId = showSeatId;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }

    public SeatEventEntity(String eventType, UUID showId, Integer quantity, UUID userId) {
        this.eventType = eventType;
        this.showId = showId;
        this.quantity = quantity;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
    }
}