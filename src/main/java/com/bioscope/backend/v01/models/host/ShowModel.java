package com.bioscope.backend.v01.models.host;

import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.TicketPrice;
import lombok.Data;

import java.util.List;

@Data
public class ShowModel {

    private String showId;
    private String showName;
    private MovieModel movie;
    private String hostName;
    private String showType;
    private String poster;
    private String arrangementType;
    private Integer capacity;
    private Integer reserved;
    private String location;
    private String screenId;
    private List<ShowSeatModel> showSeats;
    private String showDescription;
    private String showDate;
    private String showTime;
    private String showDuration;
    private List<TicketPrice> ticketPrice;
}
