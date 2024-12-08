package com.bioscope.backend.v01.models.user;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileRequestModel {

    private String name;
    private String hometown;
    private List<String> interests;

}
