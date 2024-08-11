package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.configs.ModelMapperConfiguration;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.payloads.MovieDto;
import com.bioscope.backend.v01.payloads.UserDto;
import com.bioscope.backend.v01.repos.MovieRepository;
import com.bioscope.backend.v01.services.iface.GenreService;
import com.bioscope.backend.v01.services.iface.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    private final ModelMapper modelMapper;
    MovieRepository movieRepository;
    ModelMapperConfiguration modelMapperConfiguration;
    GenreService genreService;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, ModelMapperConfiguration modelMapperConfiguration,
                            GenreService genreService, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapperConfiguration = modelMapperConfiguration;
        this.genreService = genreService;
        this.modelMapper = modelMapper;
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
    public List<MovieDto> getMovieByTitle(String title) {
        List<MovieEntity> moviesFound =  movieRepository.getMovieByTitle(title);
        if(Objects.isNull(moviesFound)){
            throw new ResourceNotFoundException("movie","title",title);
        }

        return moviesFound.stream().map(movieEntity -> modelMapperConfiguration.modelMapper()
                .map(movieEntity,MovieDto.class)).toList();
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
    public List<MovieDto> getMoviesByCast(String cast) {
        List<MovieEntity> movieEntities = movieRepository.getMovieEntitiesByCasts(cast);
        if(movieEntities.isEmpty()){
            throw new ResourceNotFoundException("Movie","cast",cast);
        }
        return movieEntities.stream().map(movieEntity ->
                modelMapperConfiguration.modelMapper().map(movieEntity,MovieDto.class)).toList();
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto) {
       List<MovieEntity> moviesFound = movieRepository.getMovieByTitle(movieDto.getTitle());
       if(
                moviesFound.stream().anyMatch(movieEntity -> movieEntity.getTitle().equals(movieDto.getTitle())) &&
                        moviesFound.stream().anyMatch(movieEntity -> movieEntity.getReleaseDate().equals(movieDto.getReleaseDate()) )
         ){
             throw new AlreadyExistsException("Movie","title",movieDto.getTitle());
         }

          MovieEntity movieToAdd = modelMapper.map(movieDto,MovieEntity.class);
          movieRepository.save(movieToAdd);
          return modelMapperConfiguration.modelMapper().map(movieToAdd,MovieDto.class);


    }

    @Override
    public void deleteMovie(MovieDto movieDto) {

    }

    @Override
    public MovieDto updateMovie(MovieDto movieDto) {
        return null;
    }

}
