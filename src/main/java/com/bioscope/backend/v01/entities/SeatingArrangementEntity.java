package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.ArrangementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeatingArrangementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID arrangementId;

    @Enumerated(EnumType.STRING)
    private ArrangementType arrangementType;

    @OneToMany(mappedBy = "seatingArrangement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatRowEntity> seatRows;

    @OneToOne
    @JoinColumn(name = "screen_id", referencedColumnName = "screenId")
    private ScreenEntity screen;


    private Integer capacity;

}
