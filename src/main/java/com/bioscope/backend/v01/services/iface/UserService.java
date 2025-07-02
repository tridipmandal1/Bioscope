package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.ReviewModel;
import com.bioscope.backend.v01.models.host.SeatingArrangementModel;
import com.bioscope.backend.v01.models.host.ShowModel;
import com.bioscope.backend.v01.models.user.SearchResult;
import com.bioscope.backend.v01.models.user.UserModel;

import java.util.List;

public interface UserService {

    List<ShowModel> trendingShows(String location);
    List<MovieModel> trendingMovies();
    UserModel getUserProfile();
    List<MovieModel> currentlyStreamingMovies(String location);
    List<UserModel> getHostsByMovieAndLocation(String movieName, String location);
    List<UserModel> getHostsByLocation(String location);
    List<ShowModel> showsByHostWithMovie(String hostId, String movieName);
    SeatingArrangementModel getSeatingArrangement(String showId);
    ReviewModel addReview(String movieId, ReviewModel reviewModel);
    List<MovieModel> getMoviesByGenre(String genre);
     SearchResult searchAnything(String query, String location);

}
