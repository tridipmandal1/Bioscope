package com.bioscope.backend.v01.models.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserProfileRequestModel {

    private String name;
    private String location;
    private List<String> interests = new ArrayList<>();

}
