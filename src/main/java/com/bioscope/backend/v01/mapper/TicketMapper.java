package com.bioscope.backend.v01.mapper;


import com.bioscope.backend.v01.entities.TicketEntity;
import com.bioscope.backend.v01.models.user.TicketModel;
import com.bioscope.backend.v01.services.iface.BucketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {


    private final ShowSeatMapper showSeatMapper;

    @Value("${r2.bucket.public.url}")
    private String bucket_public;

    public TicketMapper(ShowSeatMapper showSeatMapper) {
        this.showSeatMapper = showSeatMapper;
    }

    public TicketModel entityToModel(TicketEntity entity) {
        if (entity == null) {
            return null;
        }
        TicketModel model = new TicketModel();
        model.setId(String.valueOf(entity.getId()));
        model.setOrderId(entity.getOrderId());
        model.setPaymentId(entity.getPaymentId());
        model.setPaymentStatus(entity.getPaymentStatus());
        model.setAmount(entity.getAmount());
        model.setHostId(String.valueOf(entity.getHostId()));
        model.setShowName(entity.getShowName());
        model.setShowId(entity.getShowId());
        model.setDate(entity.getDate().toString());
        model.setStartTime(entity.getStartTime().toString());
        model.setCategory(entity.getCategory());
        model.setAllowedPersons(entity.getAllowedPersons());
        if (entity.getShowSeats() != null) {
            model.setSeats(entity.getShowSeats().stream().map(showSeatMapper::entityToModel).toList());
        }
        model.setQrCode(
                bucket_public + "/" +
                entity.getTicketQRCode());
        return model;
    }

}
