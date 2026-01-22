package com.bioscope.backend.v01.services.impl;


import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.MovieEntity;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.MovieMapper;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.MovieRepository;
import com.bioscope.backend.v01.services.iface.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MovieMapper movieMapper;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final BucketService bucketService;

    private final List<String> supportedTypes =
            List.of("jpg", "jpeg", "png", "webp", "gif");

    public MovieModel createMovie(MovieModel movieModel, MultipartFile image) {
        if (movieModel == null) {
            throw new RuntimeException("Movie model is null");
        }

        if(image == null || image.isEmpty()) {
            throw new RuntimeException("File is null or empty");
        }

        String original = Optional.ofNullable(image.getOriginalFilename())
                .orElseThrow( () -> new UnsupportedMediaTypeStatusException("File name missing"))
                .toLowerCase();

        String ext = getFileExtension(original);

        if(!supportedTypes.contains(ext)) {
            throw new UnsupportedMediaTypeStatusException("Type " + image.getContentType() + " Not supported");
        }

        String fileKey =
                bucketService.uploadFile(image);
        movieModel.setPoster(fileKey);

        MovieEntity movieEntity = movieMapper.modelToEntity(movieModel);
        List<GenreEntity> genres = new ArrayList<>();
        movieModel.getGenres().forEach(genre -> {
            Optional<GenreEntity> g = genreRepository.findByGenreName(genre);
            if (g.isPresent()) {
                genres.add(g.get());
            }else {
                GenreEntity genreEntity = new GenreEntity();
                genreEntity.setGenreName(genre);
                genreRepository.save(genreEntity);
                genres.add(genreEntity);
            }
        });
        movieEntity.setGenre(genres);
        movieEntity.setCurrentlyStreaming(true);
        movieEntity.setRating(Float.valueOf(movieModel.getRating()));
        movieRepository.save(movieEntity);
        return movieMapper.entityToModel(movieEntity);
    }


    public MovieModel updateMovie(String movieId, MovieModel movieModel, MultipartFile image) {
        if (movieId == null || movieModel == null) {
            throw new RuntimeException("Movie id or model is null");
        }
        MovieEntity movieEntity =
                movieRepository.findById(UUID.fromString(movieId)).orElseThrow(
                        () -> new ResourceNotFoundException("Movie", "id", movieId)
                );

        if (image != null){
            String original = Optional.ofNullable(image.getOriginalFilename())
                    .orElseThrow(() -> new UnsupportedMediaTypeStatusException("File name missing"))
                    .toLowerCase();

            String ext = getFileExtension(original);

            if (!supportedTypes.contains(ext)) {
                throw new UnsupportedMediaTypeStatusException("Type " + image.getContentType() + " Not supported");
            }
            String fileKey =
                    bucketService.uploadFile(image);
            movieEntity.setPoster(fileKey);
        }


        movieEntity.setTitle(movieModel.getTitle());
        movieEntity.setDescription(movieModel.getDescription());
        movieEntity.setReleaseDate(movieModel.getReleaseDate());
        movieEntity.setDuration(movieModel.getDuration());
        movieEntity.setLanguage(movieModel.getLanguage());
        movieEntity.setTrailerUrl(movieModel.getTrailerUrl());
        movieEntity.setCasts(movieModel.getCasts());
        movieEntity.setCurrentlyStreaming(movieModel.isCurrentlyStreaming());

        List<GenreEntity> genres = new ArrayList<>();

        movieModel.getGenres().forEach(genre -> {
            if(!genreRepository.existsByGenreName(genre)){
                GenreEntity newGenre = new GenreEntity();
                newGenre.setGenreName(genre);
                newGenre.addMovie(movieEntity);
                genres.add(newGenre);
            }else if (genreRepository.findByGenreName(genre).isPresent()) {
                GenreEntity genre1 = genreRepository.findByGenreName(genre).get();
                genre1.addMovie(movieEntity);
                genres.add(genre1);
            }
        });
        genreRepository.saveAll(genres);
        movieEntity.setGenre(genres);
        movieRepository.save(movieEntity);

        return movieMapper.entityToModel(movieEntity);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteMovie(String movieId) {
        if (movieId == null) {
            throw new RuntimeException("Movie id is null");
        }

        MovieEntity movie =
                movieRepository.findById(UUID.fromString(movieId)).orElseThrow(
                        () -> new ResourceNotFoundException("Movie", "id", movieId)
                );

        movieRepository.deleteById(UUID.fromString(movieId));
    }

    public List<MovieModel> getAllMovies() {
        List<MovieEntity> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return movies.stream().map(movieMapper::entityToModel).toList();
    }


    public MovieModel getMovie(String movieId) {
        if (movieId == null) {
            throw new RuntimeException("Movie id is null");
        }
        MovieEntity movieEntity = movieRepository.findById(UUID.fromString(movieId)).orElseThrow(
                () -> new ResourceNotFoundException("Movie", "id", movieId)
        );
        return movieMapper.entityToModel(movieEntity);
    }

    private String getFileExtension(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx < 0 || idx == fileName.length()-1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + fileName);
        }
        return fileName.substring(idx+1);
    }
}
