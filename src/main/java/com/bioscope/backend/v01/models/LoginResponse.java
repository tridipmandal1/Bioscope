package com.bioscope.backend.v01.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String email;
    private String role;
}
