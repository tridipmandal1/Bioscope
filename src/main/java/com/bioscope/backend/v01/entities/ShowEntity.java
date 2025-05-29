package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.ArrangementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID showId;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    private String showName;

    private String showType;

    private String poster;

    @Column(name = "arrangement_type")
    @Enumerated(EnumType.STRING)
    private ArrangementType arrangementType;

    private String location;

    private Integer capacity;

    private Integer reserved;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<PassCategoryEntity> ticketPrice;


    private String showDescription;

   @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate showDate;

    private Integer bookings;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime showTime;


    private Duration showDuration;

    @ManyToOne
    @JoinColumn(name = "screen_id", referencedColumnName = "screenId")
    private ScreenEntity screen;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSeatEntity> showSeats = new ArrayList<>();

}
