package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.PassCategoryEntity;
import com.bioscope.backend.v01.models.PassCategoryModel;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PassCategoryMapper {

    public PassCategoryModel entityToModel(PassCategoryEntity entity) {
        PassCategoryModel model = new PassCategoryModel();
        model.setId(entity.getCategory().toLowerCase(Locale.ROOT));
        model.setCategory(entity.getCategory());
        model.setReserved(entity.getReserved());
        model.setCapacity(entity.getCapacity());
        model.setPrice(entity.getPrice());
        return model;
    }

    public PassCategoryEntity modelToEntity(PassCategoryModel model) {
        PassCategoryEntity entity = new PassCategoryEntity();
        entity.setCategory(model.getCategory());
        entity.setReserved(model.getReserved());
        entity.setCapacity(model.getCapacity());
        entity.setPrice(model.getPrice());
        return entity;
    }
}
