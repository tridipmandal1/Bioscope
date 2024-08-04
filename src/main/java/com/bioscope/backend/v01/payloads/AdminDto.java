package com.bioscope.backend.v01.payloads;

import lombok.Data;

@Data
public class AdminDto {

    private String username;

    private String email;

    private String password;

    private  String role;
}
