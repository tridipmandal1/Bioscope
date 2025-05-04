package com.bioscope.backend.v01.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPrice {

    private String id;
    private String category;
    private Integer price;

    public TicketPrice(String category, Integer price) {
        this.id = category.toLowerCase(Locale.ROOT);
        this.category = category;
        this.price = price;
    }

}
