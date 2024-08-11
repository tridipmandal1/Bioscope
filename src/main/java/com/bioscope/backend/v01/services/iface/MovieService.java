package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.MovieDto;

import java.util.List;
import java.util.UUID;

public interface MovieService {

    MovieDto getMovieById(UUID movieId);
    List<MovieDto> getMovieByTitle(String title);
    List<MovieDto> getMoviesByLanguage(String language);
    List<MovieDto> getMoviesByGenreName(String genreName);
    List<MovieDto> getMoviesByCast(String cast);
    MovieDto addMovie(MovieDto movieDto);
    void deleteMovie(MovieDto movieDto);
    MovieDto updateMovie(MovieDto movieDto);
}
