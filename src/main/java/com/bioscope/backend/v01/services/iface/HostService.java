package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.SeatViewModel;
import com.bioscope.backend.v01.models.host.*;
import com.bioscope.backend.v01.models.user.UserModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HostService {

    UserModel getHostProfile();
    ScreenModel createScreen(ScreenRequestModel requestModel);
    ScreenModel getScreen(String screenId);
    ScreenModel updateScreenName(String screenId, String newName);
    SeatingArrangementModel getSeatingArrangement(String arrangementId);
    ScreenModel updateScreenSeatArrangement(String arrangementId, ScreenRequestModel requestModel);
    SeatingArrangementModel updateSeatArrangementByOneRow(String arrangementId, RowData rowData);
    List<ScreenModel> getAllScreens();
    void deleteScreen(String screenId);
    ShowModel createShow(String screenId, ShowModel showModel);
    ShowModel createOpenShow(ShowModel showModel);
    ShowModel updateOpenShow(String showId, ShowModel showModel);
    ShowModel getShow(String showId);
    ShowModel updateShow(String showId, ShowModel showModel);
    List<ShowModel> getAllShows();
    void deleteShow(String showId);
    List<MovieModel> getAllMovies();
    MovieModel getMovie(String movieId);
    MovieModel createMovie(MovieModel movieModel);
    MovieModel updateMovie(String movieId, MovieModel movieModel);
    void deleteMovie(String movieId);
    List<SeatViewModel> verifyTicket(String token);
    SeatViewModel verifyEntryPass(String token);
    String uploadImage(MultipartFile image);

}
