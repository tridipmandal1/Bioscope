package com.bioscope.backend.v01.mapper;


import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.utils.CallerContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserMapper {

    private final ShowMapper showMapper;
    private final ScreenMapper screenMapper;
    private final MovieMapper movieMapper;
    private final TicketMapper ticketMapper;

    public UserMapper(ShowMapper showMapper,
                      ScreenMapper screenMapper,
                      MovieMapper movieMapper,
                      TicketMapper ticketMapper) {
        this.showMapper = showMapper;
        this.screenMapper = screenMapper;
        this.movieMapper = movieMapper;
        this.ticketMapper = ticketMapper;
    }

    public UserModel entityToModel(UserEntity userEntity){
        if (userEntity == null){
            return null;
        }
        UserModel userModel = new UserModel();
        userModel.setUserId(String.valueOf(userEntity.getId()));
        userModel.setEmail(userEntity.getEmail());
        userModel.setRole(userEntity.getRole().name());
        userModel.setName(userEntity.getName());
        userModel.setLocation(userEntity.getLocation());
        if(userEntity.getInterests() != null){
            userModel.setInterests(userEntity.getInterests().stream()
                    .map(GenreEntity::getGenreName).toList());
        }
        if(userEntity.getShows() != null){
            if (CallerContext.getCaller().equals(Roles.HOST.name()))  {
                userModel.setShows(userEntity.getShows()
                        .stream().map(showMapper::entityToModel).toList());
            }
            if (CallerContext.getCaller().equals(Roles.USER.name())) {
                List<ShowEntity> openShows =
                        userEntity.getShows()
                                .stream()
                                .filter(showEntity -> showEntity.getMovie() == null)
                                .toList();
                userModel.setShows(openShows.stream().map(showMapper::entityToModel).toList());
            }
        }
        if(CallerContext.getCaller().equals(Roles.HOST.name()) && userEntity.getScreens() != null){
            userModel.setScreens(userEntity.getScreens()
                    .stream().map(screenMapper::entityToModel).toList());
        }
        if(userEntity.getWatchedMovies() != null){
            userModel.setWatchedMovies(userEntity.getWatchedMovies()
                    .stream().map(movieMapper::entityToModel).toList());
        }
       if (userEntity.getBookedTickets() != null){
           userModel.setBookedTickets(
                     userEntity.getBookedTickets()
                            .stream().map(ticketMapper::entityToModel).toList()
           );
       }
        return userModel;
    }


    public UserEntity modelToEntity(UserModel userModel){
        if (userModel == null){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userModel.getEmail());
        userEntity.setRole(Roles.valueOf(userModel.getRole()));
        userEntity.setName(userModel.getName());
        userEntity.setLocation(userModel.getLocation());
        if(userModel.getWatchedMovies() != null){
            userEntity.setWatchedMovies(userModel.getWatchedMovies()
                    .stream().map(movieMapper::modelToEntity).toList());
        }
        if (userModel.getShows() != null){
            userEntity.setShows(userModel.getShows()
                    .stream().map(showMapper::modelToEntity).toList());
        }
        if (userModel.getScreens() != null){
            userEntity.setScreens(userModel.getScreens()
                    .stream().map(screenMapper::modelToEntity).toList());
        }
        return userEntity;
    }
}
