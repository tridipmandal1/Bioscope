package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.configs.ModelMapperConfiguration;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.payloads.GenreDto;
import com.bioscope.backend.v01.payloads.MovieDto;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.MovieRepository;
import com.bioscope.backend.v01.services.iface.GenreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;
    MovieRepository movieRepository;
    ModelMapperConfiguration modelMapperConfiguration;

    @Override
    public GenreDto addGenre(GenreDto genreDto) {
        return null;
    }

    @Override
    public void deleteGenre(GenreDto genreDto) {

    }

    @Override
    public GenreDto updateGenre(GenreDto genreDto) {
        return null;
    }

    @Override
    public List<GenreDto> getAllGenres() {
        return List.of();
    }

    @Override
    public void addMovieToGenre(GenreDto genreDto, UUID movieId) {

    }

    @Override
    public void removeMovieFromGenre(GenreDto genreDto, UUID movieId) {

    }



    @Override
    public List<MovieDto> getMoviesByGenre(Long genreId) {
        return List.of();
    }

    @Override
    public List<MovieDto> getMoviesByGenreName(String genreName) {

        List<MovieEntity> movies = movieRepository.getMoviesByGenreName(genreName);
        if(movies.isEmpty()){
            throw new ResourceNotFoundException("movie","genre",genreName);
        }
        return movies.stream().map(movieEntity -> modelMapperConfiguration.modelMapper()
                .map(movieEntity,MovieDto.class)).toList();
    }
}
