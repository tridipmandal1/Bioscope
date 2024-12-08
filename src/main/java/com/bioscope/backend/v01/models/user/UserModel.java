package com.bioscope.backend.v01.models.user;

import lombok.Data;

import java.util.List;

@Data
public class UserModel {

    private String userId;

    private String email;

    private String name;

    private String currentLocation;

    private List<String> interests;

    private List<String> watchedMovies;
}
