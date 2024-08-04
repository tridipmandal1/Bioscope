package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.configs.ModelMapperConfiguration;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.payloads.MovieDto;
import com.bioscope.backend.v01.payloads.UserDto;
import com.bioscope.backend.v01.repos.MovieRepository;
import com.bioscope.backend.v01.services.iface.GenreService;
import com.bioscope.backend.v01.services.iface.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

    MovieRepository movieRepository;
    ModelMapperConfiguration modelMapperConfiguration;
    GenreService genreService;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository,ModelMapperConfiguration modelMapperConfiguration,
                            GenreService genreService) {
        this.movieRepository = movieRepository;
        this.modelMapperConfiguration = modelMapperConfiguration;
        this.genreService = genreService;

    }

    @Override
    public MovieDto getMovieById(UUID movieId) {

        Optional<MovieEntity> movieFound = movieRepository.findById(movieId);
        if(movieFound.isPresent()){
            return modelMapperConfiguration.modelMapper().map(movieRepository.findById(movieId), MovieDto.class);
        }

        throw new ResourceNotFoundException("Movie","id",String.valueOf(movieId));

    }

    @Override
    public MovieDto getMovieByTitle(String title) {
        MovieEntity movieFound =  movieRepository.getMovieByTitle(title);
        if(Objects.isNull(movieFound)){
            throw new ResourceNotFoundException("movie","title",title);
        }
        return modelMapperConfiguration.modelMapper().map(movieFound, MovieDto.class);
    }

    @Override
    public List<MovieDto> getMoviesByLanguage(String language) {
        List<MovieEntity> moviesFound = movieRepository.findAll();
        if(moviesFound.isEmpty()){
            throw new ResourceNotFoundException("No movies found");
        }

       return moviesFound.stream().map(movie -> modelMapperConfiguration.modelMapper()
               .map(movie,MovieDto.class)).toList();

    }

    @Override
    public List<MovieDto> getMoviesByGenreName(String genreName) {

       return genreService.getMoviesByGenreName(genreName);
    }

    @Override
    public List<MovieDto> getMoviesByDirector(String director) {
        return List.of();
    }

    @Override
    public List<MovieDto> getMoviesByCast(String cast) {
        return List.of();
    }

    @Override
    public List<MovieDto> getMoviesByReleaseYear(String releaseYear) {
        return List.of();
    }
}
