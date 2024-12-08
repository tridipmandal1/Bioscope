package com.bioscope.backend.v01.mapper;


import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserMapper {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public UserMapper(GenreRepository genreRepository, MovieRepository movieRepository){
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    /**
     * Maps UserEntity to UserModel
     * @param userEntity {@link UserEntity}
     * @return {@link UserModel}
     */
    public UserModel entityToModel(UserEntity userEntity){
        if (userEntity == null){
            return null;
        }
        UserModel userModel = new UserModel();
        userModel.setUserId(String.valueOf(userEntity.getUserID()));
        userModel.setEmail(userEntity.getEmail());
        userModel.setName(userEntity.getName());
        userModel.setCurrentLocation(userEntity.getCurrentLocation());
        List<String> userInterests = new ArrayList<>();
        List<GenreEntity> genreEntities = userEntity.getInterests();
        if(genreEntities != null){
            genreEntities.stream().map(GenreEntity :: getGenreName).forEach(userInterests::add);
            userModel.setInterests(userInterests);
        }
        List<String> userWatchedMovies = new ArrayList<>();
        List<MovieEntity> moviesWatched = userEntity.getWatchedMovies();
        if(moviesWatched != null){
            moviesWatched.stream().map(movieEntity -> movieEntity.getMovieId().toString()).forEach(userWatchedMovies::add);
            userModel.setWatchedMovies(userWatchedMovies);
        }
        return userModel;
    }

    /**
     * Maps UserModel to UserEntity
     * @param userModel {@link UserModel}
     * @return {@link UserEntity}
     */
    public UserEntity modelToEntity(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userModel.getEmail());
        userEntity.setName(userModel.getName());
        userEntity.setCurrentLocation(userModel.getCurrentLocation());
       List<String> interests =  userModel.getInterests();
         if(interests != null){
              List<GenreEntity> genreEntities = new ArrayList<>();
              interests.forEach(interest -> {
                Optional<GenreEntity> genreEntity = genreRepository.findByGenreName(interest);
                genreEntity.ifPresent(genreEntities::add);
              });
              userEntity.setInterests(genreEntities);
    }
        List<String> watchedMovies = userModel.getWatchedMovies();
        if(watchedMovies != null){
            List<MovieEntity> movieEntities = new ArrayList<>();
            watchedMovies.forEach(watchedMovie -> {
                Optional<MovieEntity> movieEntity = movieRepository.findById(UUID.fromString(watchedMovie));
                movieEntity.ifPresent(movieEntities::add);
            });
            userEntity.setWatchedMovies(movieEntities);
        }
        return userEntity;
    }
}
