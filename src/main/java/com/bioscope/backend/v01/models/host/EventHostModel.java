package com.bioscope.backend.v01.models.host;

import lombok.Data;

import java.util.List;

@Data
public class EventHostModel {

    private String host_id;
    private String name;
    private String email;
    private String location;
    private List<ScreenModel> screens;
}
