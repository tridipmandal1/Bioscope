package com.bioscope.backend.v01.models.host;

import com.bioscope.backend.v01.models.MovieModel;
import lombok.Data;

@Data
public class ShowModel {

    private String showId;
    private MovieModel movie;
    private String showType;
    private String showDescription;
    private String showDate;
    private String showTime;
}
