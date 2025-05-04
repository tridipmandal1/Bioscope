package com.bioscope.backend.v01.models.host;

import lombok.Data;

import java.util.List;

@Data
public class ScreenModel {

    private String screenId;
    private String screenName;
    private SeatingArrangementModel seatingArrangement;
    private List<ShowModel> currentShows;

}
