package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.payloads.EventHostDto;

import java.util.List;
import java.util.UUID;

public interface EventHostService {

    EventHostDto getEventHostById(UUID host_Id);
    EventHostDto getEventHostByEmail(String email);
    EventHostDto getEventHostByName(String name);
    List<EventHostDto> getEventHostByLocation(String location);
    EventHostDto getEventHostByShowId(UUID showId);
    List<EventHostDto> getEventHostByMovieId(UUID movieId);

    EventHostDto createEventHost(EventHostDto eventHostDto);
    EventHostDto updateEventHost(EventHostDto eventHostDto);
    EventHostDto deleteEventHost(UUID hostId);

    List<EventHostDto> getAllEventHosts();
    List<EventHostDto> getMultiplexes();
    List<EventHostDto> getEventHostsByMovieName(String movieName);
}
