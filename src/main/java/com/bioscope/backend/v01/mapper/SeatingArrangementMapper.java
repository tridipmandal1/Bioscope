package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import com.bioscope.backend.v01.models.host.SeatingArrangementModel;
import org.springframework.stereotype.Component;


@Component
public class SeatingArrangementMapper {

    private final SeatRowMapper seatRowMapper;
    public SeatingArrangementMapper(SeatRowMapper seatRowMapper) {
        this.seatRowMapper = seatRowMapper;
    }

    /**
     * Maps SeatingArrangementEntity to SeatingArrangementModel
     * @param entity {@link SeatingArrangementEntity}
     * @return  {@link SeatingArrangementModel}
     */
    SeatingArrangementModel entityToModel (SeatingArrangementEntity entity) {
        if (entity == null) {
            return null;
        }
        SeatingArrangementModel model = new SeatingArrangementModel();
        model.setArrangementId(entity.getArrangement_id().toString());
        model.setArrangementType(entity.getArrangementType());
        if (entity.getSeatRow() != null) {
            model.setSeatRow(
                    entity.getSeatRow().stream()
                            .map(seatRowMapper::entityToModel)
                            .toList()
            );
        }
        model.setCapacity(entity.getCapacity());
        model.setBookedSeats(entity.getBookedSeats());
        model.setPrice(entity.getPrice());
        return model;
    }

    /**
     * Maps SeatingArrangementModel to SeatingArrangementEntity
     * @param model {@link SeatingArrangementModel}
     * @return {@link SeatingArrangementEntity}
     */
    SeatingArrangementEntity modelToEntity (SeatingArrangementModel model) {
        if (model == null) {
            return null;
        }
        SeatingArrangementEntity entity = new SeatingArrangementEntity();
        entity.setArrangementType(model.getArrangementType());
        if (model.getSeatRow() != null) {
            entity.setSeatRow(
                    model.getSeatRow().stream()
                            .map(seatRowMapper::modelToEntity)
                            .toList()
            );
        }
        entity.setCapacity(model.getCapacity());
        entity.setBookedSeats(model.getBookedSeats());
        entity.setPrice(model.getPrice());
        return entity;
    }
}
