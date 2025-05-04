package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.SeatEntity;
import com.bioscope.backend.v01.models.host.SeatModel;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    SeatModel entityToModel(SeatEntity entity) {
        if (entity == null) {
            return null;
        }
        SeatModel model = new SeatModel();
        model.setId(entity.getId());
        model.setPrice(entity.getPrice());
        return model;
    }

    SeatEntity modelToEntity(SeatModel model) {
        if (model == null) {
            return null;
        }
        SeatEntity entity = new SeatEntity();
        entity.setPrice(model.getPrice());
        return entity;
    }
}
