package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.EventHostEntity;
import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import com.bioscope.backend.v01.entities.ShowEntity;

public class ScreenDto {

    private String screenName;

    private SeatingArrangementEntity seatingArrangement;

    private EventHostEntity eventHost;

    private ShowEntity show;
}
