package com.bioscope.backend.v01.models;

import lombok.Data;

@Data
public class PassCategoryModel {

    private String id;
    private String category;
    private Double price;
    private Integer reserved;
    private Integer capacity;
}
