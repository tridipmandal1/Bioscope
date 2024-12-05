package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.EventHostEntity;
import com.bioscope.backend.v01.models.host.EventHostModel;
import org.springframework.stereotype.Component;

@Component
public class EventHostMapper {

    private final ScreenMapper screenMapper;

    public EventHostMapper(ScreenMapper screenMapper) {
        this.screenMapper = screenMapper;
    }

    /**
     * Maps {@link EventHostModel} to {@link EventHostEntity}
     * @param hostEntity {@link EventHostEntity}
     * @return {@link EventHostModel}
     */
     EventHostModel entityToModel(EventHostEntity hostEntity) {
        if ( hostEntity == null ) {
            return null;
        }
        EventHostModel hostModel = new EventHostModel();
        hostModel.setHost_id(String.valueOf(hostEntity.getHost_id()));
        hostModel.setName(hostEntity.getName());
        hostModel.setEmail(hostEntity.getEmail());
        hostModel.setLocation(hostEntity.getLocation());
        if (hostEntity.getScreens() != null) {
            hostModel.setScreens(
                    hostEntity.getScreens().stream()
                            .map(screenMapper ::entityToModel).toList()
            );
        }
        return hostModel;
    }

    /**
     * Maps {@link EventHostEntity} to {@link EventHostModel}
     * @param hostModel {@link EventHostModel}
     * @return {@link EventHostEntity}
     */

    EventHostEntity modelToEntity(EventHostModel hostModel) {
        if (hostModel == null) {
            return null;
        }
        EventHostEntity hostEntity = new EventHostEntity();
        hostEntity.setName(hostModel.getName());
        hostEntity.setEmail(hostModel.getEmail());
        hostEntity.setLocation(hostModel.getLocation());
        if (hostModel.getScreens() != null) {
            hostEntity.setScreens(
                    hostModel.getScreens().stream()
                            .map(screenMapper::modelToEntity).toList()
            );
        }
        return hostEntity;
    }

}
