package com.bioscope.backend.v01.models.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequestModel {

    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String role;

}
