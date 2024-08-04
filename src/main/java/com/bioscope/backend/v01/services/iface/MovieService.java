package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.MovieDto;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    MovieDto getMovieById(UUID movieId);
    MovieDto getMovieByTitle(String title);
    List<MovieDto> getMoviesByLanguage(String language);
    List<MovieDto> getMoviesByGenreName(String genreName);
    List<MovieDto> getMoviesByDirector(String director);
    List<MovieDto> getMoviesByCast(String cast);
    List<MovieDto> getMoviesByReleaseYear(String releaseYear);
}
