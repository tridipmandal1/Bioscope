package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.models.ApiResponse;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.services.impl.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v01/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @PostMapping(value = "/movie", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieModel> createMovie(@RequestBody MovieModel movieModel, @RequestParam MultipartFile image) {
        return
                new ResponseEntity<>(adminService.createMovie(movieModel, image), HttpStatus.CREATED);
    }

    @PutMapping(value = "/movie/{movieId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieModel> updateMovie(
            @PathVariable String movieId,
            @RequestBody MovieModel movieModel,
            @RequestParam(required = false) MultipartFile image) {
        return
                new ResponseEntity<>(adminService.updateMovie(movieId, movieModel, image), HttpStatus.OK);
    }

    @DeleteMapping(value = "/movie/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable String movieId) {
        adminService.deleteMovie(movieId);
        var response = ApiResponse.builder()
                .status(true)
                .message("Successfully deleted")
                .build();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/movie/{movieId}")
    public ResponseEntity<MovieModel> getMovie(@PathVariable String movieId) {
        return
                new ResponseEntity<>(adminService.getMovie(movieId), HttpStatus.OK);
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieModel>> getMovies() {
        return
                new ResponseEntity<>(adminService.getAllMovies(), HttpStatus.OK);
    }
}
