package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.models.TicketPrice;
import com.bioscope.backend.v01.models.host.*;
import com.bioscope.backend.v01.models.host.ShowSeatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ShowMapper {

    private final MovieMapper movieMapper;
    private final ShowSeatMapper showSeatMapper;

    public ShowMapper (MovieMapper movieMapper, ShowSeatMapper showSeatMapper) {
        this.movieMapper = movieMapper;
        this.showSeatMapper = showSeatMapper;
    }

    public ShowModel entityToModel(ShowEntity showEntity) {
        if (showEntity == null) {
            return null;
        }
        ShowModel showModel = new ShowModel();
        UserEntity host = showEntity.getUser();
        showModel.setShowId(String.valueOf(showEntity.getShowId()));
        showModel.setShowName(showEntity.getShowName());
        if (showEntity.getMovie() != null) {
            showModel.setMovie(movieMapper.entityToModel(showEntity.getMovie()));
        }
        showModel.setHostName(host.getName());
        if(showEntity.getArrangementType() != null) {
            showModel.setArrangementType(showEntity.getArrangementType().name());
        }
        showModel.setShowType(showEntity.getShowType());
        showModel.setPoster(showEntity.getPoster());
        showModel.setLocation(showEntity.getLocation());
        showModel.setShowDescription(showEntity.getShowDescription());
        showModel.setShowDate(showEntity.getShowDate().toString());
        showModel.setShowTime(showEntity.getShowTime().toString());
        showModel.setShowDuration(showEntity.getShowDuration().toString());
        showModel.setCapacity(showEntity.getCapacity());
        showModel.setReserved(showEntity.getReserved());
        if(showEntity.getScreen() != null) {
            showModel.setScreenId(String.valueOf(showEntity.getScreen().getScreenId()));
        }
        if(!showEntity.getShowSeats().isEmpty()){
           List<ShowSeatModel>  showSeats =
                   showEntity.getShowSeats().stream()
                           .map(showSeatMapper::entityToModel).toList();
            showModel.setShowSeats(showSeats);
        }
        if (showEntity.getTicketPrice() != null) {
            showModel.setTicketPrice(
                    showEntity.getTicketPrice()
                            .entrySet()
                            .stream()
                            .map(entry -> new TicketPrice(entry.getKey(), entry.getValue()))
                            .toList()
            );
        }
        return showModel;
    }

    public ShowEntity modelToEntity(ShowModel showModel) {
        if (showModel == null) {
            return null;
        }
        ShowEntity showEntity = new ShowEntity();
        if (showModel.getMovie() != null) {
            showEntity.setMovie(movieMapper.modelToEntity(showModel.getMovie()));
        }
        showEntity.setShowName(showModel.getShowName());
        showEntity.setShowType(showModel.getShowType());
        showEntity.setPoster(showModel.getPoster());
        if(showModel.getArrangementType() != null) {
            showEntity.setArrangementType(ArrangementType.valueOf(showModel.getArrangementType()));
        }
        showEntity.setLocation(showModel.getLocation());
        showEntity.setShowDescription(showModel.getShowDescription());
        showEntity.setShowDate(LocalDate.parse(showModel.getShowDate()));
        showEntity.setShowTime(LocalTime.parse(showModel.getShowTime()));
        showEntity.setShowDuration(Duration.parse(showModel.getShowDuration()));
        showEntity.setReserved(showModel.getReserved());
        showEntity.setCapacity(showModel.getCapacity());
        if (showModel.getTicketPrice() != null) {
            Map<String, Integer> prices = new HashMap<>();
            showModel.getTicketPrice().forEach(
                    ticketPrice -> prices.put(ticketPrice.getCategory(), ticketPrice.getPrice())
            );
            showEntity.setTicketPrice(prices);
        }
        return showEntity;
    }

}
