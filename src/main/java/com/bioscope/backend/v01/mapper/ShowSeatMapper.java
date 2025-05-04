package com.bioscope.backend.v01.mapper;


import com.bioscope.backend.v01.constants.SeatId;
import com.bioscope.backend.v01.entities.ShowSeatEntity;
import com.bioscope.backend.v01.models.host.ShowSeatModel;
import org.springframework.stereotype.Component;

@Component
public class ShowSeatMapper {

    public ShowSeatModel entityToModel(ShowSeatEntity showSeatEntity) {
        if (showSeatEntity == null) {
            return null;
        } else {
            ShowSeatModel showSeatModel = new ShowSeatModel();
            showSeatModel.setSSId(showSeatEntity.getId().toString());
            showSeatModel.setSeatStatus(showSeatEntity.getSeatStatus().toString());
            showSeatModel.setSeatId(
                    new SeatId(
                            showSeatEntity.getSeat().getId().getSeatingArrangementId(),
                            showSeatEntity.getSeat().getId().getRowIndex(),
                            showSeatEntity.getSeat().getId().getSeatNumber()
                    )
            );
            return showSeatModel;
        }
    }

}
