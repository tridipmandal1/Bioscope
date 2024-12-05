package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.EventHostEntity;
import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.models.host.EventHostModel;
import com.bioscope.backend.v01.models.host.EventHostRequestModel;
import com.bioscope.backend.v01.repos.EventHostRepository;
import com.bioscope.backend.v01.services.iface.EventHostService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventHostServiceImpl implements EventHostService {

    private final EventHostRepository eventHostRepository;

    public EventHostServiceImpl(
        EventHostRepository eventHostRepository
    ){
        this.eventHostRepository = eventHostRepository;
    }
    @Override
    public EventHostModel createEventHost(EventHostRequestModel eventHostRequestModel) {
        String email = eventHostRequestModel.getEmail();
        Optional<EventHostEntity> hostFound = eventHostRepository.findByEmail(email);
        if (hostFound.isPresent()) {
            throw new AlreadyExistsException("Host with email: " + email + " already exists");
        }
        EventHostEntity hostEntity = new EventHostEntity();
        hostEntity.setEmail(email);
        hostEntity.setPassword(eventHostRequestModel.getPassword()); // need to encrypt
    }
}
