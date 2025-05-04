package com.bioscope.backend.v01.models.user;

import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.host.ScreenModel;
import com.bioscope.backend.v01.models.host.SeatModel;
import com.bioscope.backend.v01.models.host.ShowModel;
import lombok.Data;

import java.util.List;

@Data
public class UserModel {

    private String userId;
    private String email;
    private String role;
    private String name;
    private String location;
    private List<String> interests;
    private List<ShowModel> shows;
    private List<ScreenModel> screens;
    private List<MovieModel> watchedMovies;
    private List<TicketModel> bookedTickets;
}
