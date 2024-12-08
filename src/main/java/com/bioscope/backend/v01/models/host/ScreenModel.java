package com.bioscope.backend.v01.models.host;

import lombok.Data;

import java.util.List;

@Data
public class ScreenModel {

    private String screenId;
    private String screenName;
    private String eventHostId;
    private List<ShowModel> currentShows;
    private SeatingArrangementModel seatingArrangement;
    private int totalAvailableSeats;

}
