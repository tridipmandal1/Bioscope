package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.ScreenEntity;
import com.bioscope.backend.v01.entities.ShowEntity;
import com.bioscope.backend.v01.models.host.ScreenModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ScreenMapper {

    private final  SeatingArrangementMapper seatingArrangementMapper;
    private final ShowMapper showMapper;

    public ScreenMapper(SeatingArrangementMapper seatingArrangementMapper,
                        ShowMapper showMapper) {
        this.seatingArrangementMapper = seatingArrangementMapper;
        this.showMapper = showMapper;
    }

    public ScreenModel entityToModel(ScreenEntity screenEntity) {
        if (screenEntity == null) {
            return null;
        }
        ScreenModel screenModel = new ScreenModel();
        screenModel.setScreenId(String.valueOf(screenEntity.getScreenId()));
        screenModel.setScreenName(screenEntity.getScreenName());
        Predicate<ShowEntity> isShowDateBeforeToday = showEntity ->
                showEntity.getShowDate().isAfter(LocalDate.now());
        if (screenEntity.getShows() != null) {
            screenModel.setCurrentShows(
                    screenEntity.getShows().stream()
                            .filter(isShowDateBeforeToday)
                            .map(showMapper ::entityToModel).collect(Collectors.toList())
            );
        }
        if (screenEntity.getSeatingArrangement() != null) {
            screenModel.setSeatingArrangement(
                    seatingArrangementMapper.entityToModel(screenEntity.getSeatingArrangement())
            );
        }
        return screenModel;
    }

    ScreenEntity modelToEntity(ScreenModel screenModel) {
        if (screenModel == null) {
            return null;
        }
        ScreenEntity screenEntity= new ScreenEntity();
        screenEntity.setScreenName(screenModel.getScreenName());
        if (screenModel.getCurrentShows() != null) {
            screenEntity.setShows(
                    screenModel.getCurrentShows().stream()
                            .map(showMapper ::modelToEntity).collect(Collectors.toList())
            );
        }
        if (screenModel.getSeatingArrangement() != null) {
            screenEntity.setSeatingArrangement(
                    seatingArrangementMapper.modelToEntity(screenModel.getSeatingArrangement())
            );
        }
        return screenEntity;
    }
}
