package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.SeatRowEntity;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.models.host.SeatModel;
import com.bioscope.backend.v01.models.host.SeatRowModel;
import com.bioscope.backend.v01.repos.SeatRowRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SeatRowMapper {

    private final SeatMapper seatMapper;
    private final SeatRowRepository seatRowRepository;

    public SeatRowMapper(SeatMapper seatMapper, SeatRowRepository seatRowRepository) {
        this.seatMapper = seatMapper;
        this.seatRowRepository = seatRowRepository;
    }

    /**
     * Maps SeatRowEntity to SeatRowModel
     * @param entity {@link SeatRowEntity}
     * @return {@link SeatRowModel}
     */
     SeatRowModel entityToModel(SeatRowEntity entity) {
         if (entity == null) {
             return null;
         }
        SeatRowModel model = new SeatRowModel();
        model.setRowId(entity.getRowId().toString());
        model.setRowNumber(entity.getRowNumber());
        if (entity.getSeats() != null) {
            model.setSeats(entity.getSeats().stream()
                    .map(seatMapper::entityToModel).toList());
        }
        return model;
    }

    /**
     * Maps SeatRowModel to SeatRowEntity
     * @param model {@link SeatRowModel}
     * @return {@link SeatRowEntity}
     */
    SeatRowEntity modelToEntity(SeatRowModel model) {
        if (model == null) {
            return null;
        }
        SeatRowEntity entity = new SeatRowEntity();
        entity.setRowNumber(model.getRowNumber());
        if (model.getSeats() != null) {
            entity.setSeats(model.getSeats().stream()
                    .map(seatMapper::modelToEntity).toList());
        }
        return entity;
    }
}
