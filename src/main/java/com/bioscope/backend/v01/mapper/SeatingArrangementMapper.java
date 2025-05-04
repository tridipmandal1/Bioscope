package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.entities.SeatingArrangementEntity;
import com.bioscope.backend.v01.models.host.SeatRowModel;
import com.bioscope.backend.v01.models.host.SeatingArrangementModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;


@Component
public class SeatingArrangementMapper {

    private final SeatRowMapper seatRowMapper;
    public SeatingArrangementMapper(SeatRowMapper seatRowMapper) {
        this.seatRowMapper = seatRowMapper;
    }

    public SeatingArrangementModel entityToModel (SeatingArrangementEntity entity) {
        if (entity == null) {
            return null;
        }
        SeatingArrangementModel model = new SeatingArrangementModel();
        model.setArrangementId(entity.getArrangementId().toString());
        model.setArrangementType(entity.getArrangementType().toString());
        if (entity.getSeatRows() != null) {

            Comparator<SeatRowModel> indexComparator =
                    Comparator.comparing(SeatRowModel::getRowIndex);

                   List<SeatRowModel> usSeats =  entity.getSeatRows().stream()
                            .map(seatRowMapper::entityToModel)
                           .sorted(indexComparator).toList();
            model.setSeatRow(usSeats);
        }
        model.setCapacity(entity.getCapacity());
        return model;
    }


    public SeatingArrangementEntity modelToEntity (SeatingArrangementModel model) {
        if (model == null) {
            return null;
        }
        SeatingArrangementEntity entity = new SeatingArrangementEntity();
        entity.setArrangementType(ArrangementType.valueOf(model.getArrangementType()));
        if (model.getSeatRow() != null) {
            entity.setSeatRows(
                    model.getSeatRow().stream()
                            .map(seatRowMapper::modelToEntity)
                            .toList()
            );
        }
        entity.setCapacity(model.getCapacity());
        return entity;
    }
}
