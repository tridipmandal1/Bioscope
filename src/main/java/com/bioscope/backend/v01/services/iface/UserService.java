package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.EventHostDto;
import com.bioscope.backend.v01.payloads.MovieDto;
import com.bioscope.backend.v01.payloads.UserDto;

import java.util.UUID;
import java.util.List;

public interface UserService {

    UserDto getUserById(UUID id);
    UserDto getUserByEmail(String email);
    UserDto getUserByPhoneNumber(String phoneNumber);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUser(UUID id);
    List<EventHostDto> getEventHostsByCity(String city);
    List<EventHostDto> getEventHostsByGenre(String genre);
    List<MovieDto> getWatchedMovies(UUID userId);
    List<MovieDto> getRecommendedMovies(UUID userId);
    Void addWatchedMovie(UUID userId, UUID movieId);
    void bookSeats(UUID userId, UUID eventId, int seats);
    void cancelBooking(UUID userId, UUID eventId, int seats);


}
