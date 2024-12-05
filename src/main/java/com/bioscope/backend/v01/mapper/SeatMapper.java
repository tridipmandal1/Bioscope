package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.SeatEntity;
import com.bioscope.backend.v01.enums.SeatCategory;
import com.bioscope.backend.v01.models.host.SeatModel;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    /**
     * SeatEntity to SeatModel
     * @param entity {@link SeatEntity}
     * @return {@link SeatModel}
     */
    SeatModel entityToModel(SeatEntity entity) {
        if (entity == null) {
            return null;
        }
        SeatModel model = new SeatModel();
        model.setId(String.valueOf(entity.getId()));
        model.setSeatNumber(entity.getSeatNumber());
        model.setBookingStatus(entity.getBookingStatus());
        model.setCategory(entity.getSeatCategory().name());
        return model;
    }

    /**
     * SeatModel to SeatEntity
     * @param model {@link SeatModel}
     * @return {@link SeatEntity}
     */
    SeatEntity modelToEntity(SeatModel model) {
        if (model == null) {
            return null;
        }
        SeatEntity entity = new SeatEntity();
        entity.setSeatNumber(model.getSeatNumber());
        entity.setBookingStatus(model.getBookingStatus());
        entity.setSeatCategory(SeatCategory.valueOf(model.getCategory()));
        return entity;
    }
}
