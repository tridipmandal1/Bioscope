package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.GenreDto;
import com.bioscope.backend.v01.payloads.MovieDto;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    GenreDto addGenre(GenreDto genreDto);
    void deleteGenre(GenreDto genreDto);
    GenreDto updateGenre(GenreDto genreDto);
    List<GenreDto> getAllGenres();
    void addMovieToGenre(GenreDto genreDto, UUID movieId);
    void removeMovieFromGenre(GenreDto genreDto, UUID movieId);
    List<MovieDto> getMoviesByGenre(Long genreId);
    List<MovieDto> getMoviesByGenreName(String genreName);

}
