package com.bioscope.backend.v01.payloads;

import com.bioscope.backend.v01.entities.EventHostEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import lombok.Data;

@Data
public class ShowDto {

    private EventHostEntity host;

    private String showName;

    private MovieEntity movie;

    private String showTime;

    private SeatingArrangementEntity seatingArrangement;


}
