package com.bioscope.backend.v01.mapper;

import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.models.GenreModel;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {


    public GenreModel entityToModel(GenreEntity genreEntity) {
        if (genreEntity == null) {
            return null;
        }
        GenreModel genreModel = new GenreModel();
        genreModel.setGenreId(genreEntity.getGenreId().toString());
        genreModel.setGenreName(genreEntity.getGenreName());
        genreModel.setMovies(genreEntity.getMovies().stream()
                .map(MovieEntity::getTitle).toList());
        return genreModel;
    }

    public GenreEntity modelToEntity(GenreModel genreModel) {
        if (genreModel == null) {
            return null;
        }
        GenreEntity genreEntity = new GenreEntity();
        genreEntity.setGenreName(genreModel.getGenreName());
        return genreEntity;
    }
}
