package com.bioscope.backend.v01.models;

import lombok.*;

@Data
@Builder
public class ApiResponse {
    private String message;
    private boolean status;

}
