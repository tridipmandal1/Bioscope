package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID rowId;

    @Column(name = "row_index", nullable = false)
    private String rowIndex;

    @Enumerated(EnumType.STRING)
    private SeatCategory seatCategory;

    @OneToMany(mappedBy = "seatRowEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seats = new ArrayList<>();

    @ElementCollection
    private List<Integer> passageAfterwards;

    @ManyToOne
    @JoinColumn(name = "arrangement_id", referencedColumnName = "arrangementId")
    private SeatingArrangementEntity seatingArrangement;

    public void addSeat(SeatEntity seat) {
        seats.add(seat);
        seat.setSeatRowEntity(this);
    }
}
