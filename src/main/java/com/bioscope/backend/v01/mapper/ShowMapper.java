package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.models.host.ShowModel;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class ShowMapper {

    private final MovieMapper movieMapper;

    public ShowMapper (MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    /**
     * Maps ShowEntity to ShowModel
     * @param showEntity {@link ShowEntity}
     * @return {@link ShowModel}
     */
    ShowModel entityToModel (ShowEntity showEntity) {
        if (showEntity == null) {
            return null;
        }
        ShowModel showModel = new ShowModel();
        showModel.setShowId(String.valueOf(showEntity.getShow_id()));
        showModel.setMovie(movieMapper.entityToModel(showEntity.getMovie()));
        showModel.setShowType(showEntity.getShowType());
        showModel.setShowDescription(showEntity.getShowDescription());
        showModel.setShowDate(showEntity.getShowDate());
        showModel.setShowTime(showEntity.getShowTime().toString());
        return showModel;
    }

    /**
     * Maps ShowModel to ShowEntity
     * @param showModel {@link ShowModel}
     * @return {@link ShowEntity}
     */
    ShowEntity modelToEntity(ShowModel showModel) {
        if (showModel == null) {
            return null;
        }
        ShowEntity showEntity = new ShowEntity();
        showEntity.setMovie(movieMapper.modelToEntity(showModel.getMovie()));
        showEntity.setShowType(showModel.getShowType());
        showEntity.setShowDescription(showModel.getShowDescription());
        showEntity.setShowDate(showModel.getShowDate());
        showEntity.setShowTime(LocalTime.parse(showModel.getShowTime()));
        return showEntity;
    }

}
