package com.bioscope.backend.v01.constants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SeatId implements Serializable {

        @JdbcType(VarcharUUIDJdbcType.class)
        @Column(columnDefinition = "CHAR(36)")
        private UUID seatingArrangementId;
        private String rowIndex;
        private Integer seatNumber;

}
