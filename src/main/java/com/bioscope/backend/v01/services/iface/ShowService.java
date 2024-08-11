package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.SeatArrDto;
import com.bioscope.backend.v01.payloads.ShowDto;
import java.util.List;

import java.util.UUID;

public interface ShowService {

    ShowDto getShowById(String showId);
    ShowDto getShowByMovieId(UUID movieId);
    List<ShowDto> getShowsByEventHostId(UUID eventHostId);
    List<ShowDto> getShowsByLocation(String location);
    List<ShowDto> getShowsByDate(String date);
    List<ShowDto> getShowsByTime(String time);
    List<ShowDto> getShowsByMovieGenre(String genre);
    List<ShowDto> getShowsByMovieLanguage(String language);
    List<ShowDto> getShowsByCast(String cast);
    ShowDto createShow(ShowDto showDto);
    ShowDto updateShow(ShowDto showDto);
    void deleteShow(UUID showId);
    SeatArrDto getSeatingArrangementByShowId(UUID showId);

}
