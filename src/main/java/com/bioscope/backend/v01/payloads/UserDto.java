package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.GenreEntity;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String email;

    private String password;

    private String firstName;

    private String lastName;


    private String phoneNumber;

    private String hometown;

    private List<GenreEntity> interests;
}
