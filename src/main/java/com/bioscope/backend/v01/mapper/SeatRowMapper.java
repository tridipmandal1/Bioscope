package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.SeatRowEntity;
import com.bioscope.backend.v01.enums.SeatCategory;
import com.bioscope.backend.v01.models.host.SeatModel;
import com.bioscope.backend.v01.models.host.SeatRowModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatRowMapper {

    private final SeatMapper seatMapper;

    public SeatRowMapper(SeatMapper seatMapper) {
        this.seatMapper = seatMapper;
    }

     SeatRowModel entityToModel(SeatRowEntity entity) {
         if (entity == null) {
             return null;
         }
        SeatRowModel model = new SeatRowModel();
        model.setRowId(entity.getRowId().toString());
        model.setRowIndex(entity.getRowIndex());
        model.setSeatCategory(entity.getSeatCategory().name());
        if (entity.getSeats() != null) {

            model.setSeats(entity.getSeats().stream()
                    .map(seatMapper::entityToModel).collect(Collectors.toList()));
//            List<SeatModel> seats = new ArrayList<>();
//            for (int i = 1; i <= entity.getSeats().size(); i++) {
//                if (entity.getPassageAfterwards().isEmpty()) {
//                    seats = entity.getSeats().stream()
//                            .map(seatMapper::entityToModel).toList();
//                    break;
//                } else {
//                    seats.add(seatMapper.entityToModel(entity.getSeats().get(i - 1)));
//                    if(entity.getPassageAfterwards().contains(i)){
//                        seats.add(new SeatModel(null, -1, null));
//                    }
//                }
//            }
//            model.setSeats(seats);
        }
        if (entity.getPassageAfterwards() != null) {
            model.setPassageAfterwards(entity.getPassageAfterwards());
        }
        return model;
    }

    SeatRowEntity modelToEntity(SeatRowModel model) {
        if (model == null) {
            return null;
        }
        SeatRowEntity entity = new SeatRowEntity();
        entity.setRowIndex(model.getRowIndex());
        entity.setSeatCategory(SeatCategory.valueOf(model.getSeatCategory()));
        if (model.getSeats() != null) {
            entity.setSeats(model.getSeats().stream()
                    .map(seatMapper::modelToEntity).toList());
        }
        if (model.getPassageAfterwards() != null) {
            entity.setPassageAfterwards(model.getPassageAfterwards());
        }
        return entity;
    }
}
