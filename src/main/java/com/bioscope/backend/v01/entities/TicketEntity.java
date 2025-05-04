package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    private String hostId;

    @Column(name = "show_id")
    private String showId;

    private String showName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @OneToMany(mappedBy = "ticket", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<ShowSeatEntity> showSeats = new ArrayList<>();

    // if pass

    private String category;

    @Column(name = "allowed_persons")
    private Integer allowedPersons;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    private String ticketQRCode;


    public void addShowSeat(ShowSeatEntity showSeat) {
        showSeats.add(showSeat);
        showSeat.setTicket(this);
    }
}
