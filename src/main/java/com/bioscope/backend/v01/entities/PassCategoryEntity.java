package com.bioscope.backend.v01.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String category;
    private Double price;
    private Integer reserved;
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "show_id", referencedColumnName = "showId")
    private ShowEntity show;
}
