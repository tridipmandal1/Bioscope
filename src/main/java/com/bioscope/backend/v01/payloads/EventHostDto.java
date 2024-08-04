package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.ShowEntity;

import lombok.Data;

import java.util.List;

@Data
public class EventHostDto {

    private String host_name;


    private String host_email;

    private String password;

    private  String location;


    private boolean isMultiplex;
}
