package com.bioscope.backend.v01.mapper;


import com.bioscope.backend.v01.entities.TicketEntity;
import com.bioscope.backend.v01.models.user.TicketModel;
import com.bioscope.backend.v01.services.iface.BucketService;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {


    private final ShowSeatMapper showSeatMapper;
    private final BucketService bucketService;

    public TicketMapper(ShowSeatMapper showSeatMapper, BucketService bucketService) {
        this.showSeatMapper = showSeatMapper;
        this.bucketService = bucketService;
    }

    public TicketModel entityToModel(TicketEntity entity) {
        if (entity == null) {
            return null;
        }
        TicketModel model = new TicketModel();
        model.setId(String.valueOf(entity.getId()));
        model.setHostId(String.valueOf(entity.getHostId()));
        model.setShowName(entity.getShowName());
        model.setDate(entity.getDate().toString());
        model.setStartTime(entity.getStartTime().toString());
        model.setCategory(entity.getCategory());
        model.setAllowedPersons(entity.getAllowedPersons());
        if (entity.getShowSeats() != null) {
            model.setSeats(entity.getShowSeats().stream().map(showSeatMapper::entityToModel).toList());
        }
        model.setQrCode(bucketService.preSignedUrl(entity.getTicketQRCode()));
        return model;
    }

}
