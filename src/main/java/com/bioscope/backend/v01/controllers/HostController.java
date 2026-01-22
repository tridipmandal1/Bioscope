package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.models.ApiResponse;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.host.*;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.services.iface.HostService;
import com.bioscope.backend.v01.services.iface.QRCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v01/host")
public class HostController {

    private final HostService hostService;
    private final QRCodeService qrCodeService;

    public HostController(HostService hostService,
                          QRCodeService qrCodeService) {
        this.hostService = hostService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getHostModel() {
        UserModel hostModel = hostService.getHostProfile();
        return new  ResponseEntity<>(hostModel, HttpStatus.OK);
    }

    @PostMapping(value = "/create-screen", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScreenModel> createScreen(@RequestBody ScreenRequestModel requestModel) {
        return
                new ResponseEntity<>(hostService.createScreen(requestModel), HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-screen/{screenId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScreenModel> getScreen(@PathVariable String screenId) {

        ScreenModel sc = hostService.getScreen(screenId);
        return
                new ResponseEntity<>(sc, HttpStatus.OK);
    }

    @PutMapping(value = "/update-screen/{screenId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScreenModel> updateScreen(@PathVariable String screenId,
                                                    @RequestParam String newName) {
        return
                new ResponseEntity<>(hostService.updateScreenName(screenId, newName), HttpStatus.OK);
    }

    @PutMapping(value = "/update-seating/{arrangementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScreenModel> updateScreenSeatArrangement(
            @PathVariable String arrangementId,
            @RequestBody ScreenRequestModel requestModel) {
        return
                new ResponseEntity<>(hostService.updateScreenSeatArrangement(arrangementId, requestModel), HttpStatus.OK);
    }

    @PutMapping(value = "/update-seating-row/{arrangementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeatingArrangementModel> updateSeatArrangementByOneRow(
            @PathVariable String arrangementId,
            @RequestBody RowData rowData) {
        return
                new ResponseEntity<>(hostService.updateSeatArrangementByOneRow(arrangementId, rowData), HttpStatus.OK);
    }

    @GetMapping(value = "/screens", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScreenModel>> getScreens() {
        return
                new ResponseEntity<>(hostService.getAllScreens(), HttpStatus.OK);
    }

    @GetMapping(value = "/seating/{arrangementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeatingArrangementModel> getSeatingArrangement(@PathVariable String arrangementId) {
        return
                new ResponseEntity<>(hostService.getSeatingArrangement(arrangementId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete-screen/{screenId}")
    public ResponseEntity<?> deleteScreen(@PathVariable String screenId) {
        hostService.deleteScreen(screenId);
        var response = ApiResponse.builder()
                .status(true)
                .message("Successfully deleted")
                .build();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "create/show", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> createShow(
            @RequestParam String screenId,
            @RequestBody ShowModel showModel) {
        return
                new ResponseEntity<>(hostService.createShow(screenId, showModel), HttpStatus.CREATED);
    }

    @PostMapping(value = "/create/open-show", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> createOpenShow(@RequestBody ShowModel showModel) {
        return
                new ResponseEntity<>(hostService.createOpenShow(showModel), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/open-show/{showId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> updateOpenShow(
            @PathVariable String showId,
            @RequestBody ShowModel showModel) {
        return
                new ResponseEntity<>(hostService.updateOpenShow(showId, showModel), HttpStatus.OK);
    }
    @GetMapping(value = "/show/{showId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> getShow(@PathVariable String showId) {
        return
                new ResponseEntity<>(hostService.getShow(showId), HttpStatus.OK);
    }

    @PutMapping(value = "/show/{showId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> updateShow(
            @PathVariable String showId,
            @RequestBody ShowModel showModel) {

        return
                new ResponseEntity<>(hostService.updateShow(showId, showModel), HttpStatus.OK);
    }

    @GetMapping(value = "/shows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShowModel>> getShows() {
        return
                new ResponseEntity<>(hostService.getAllShows(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/show/{showId}")
    public ResponseEntity<?> deleteShow(@PathVariable String showId) {
        hostService.deleteShow(showId);
        var response = ApiResponse.builder()
                .status(true)
                .message("Successfully deleted")
                .build();
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @GetMapping(value = "/movies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieModel>> getMovies() {
        return
                new ResponseEntity<>(hostService.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping(value = "/movie/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieModel> getMovie(@PathVariable String movieId) {
        return
                new ResponseEntity<>(hostService.getMovie(movieId), HttpStatus.OK);
    }



    @PostMapping(value = "/verify-ticket",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyTicket(@RequestParam("file") MultipartFile file) {

        try {
            String token = qrCodeService.readQRCode(file);
            return
                    new ResponseEntity<>(hostService.verifyTicket(token), HttpStatus.OK);
        } catch (IOException e) {
            var response = ApiResponse.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping(value = "/verify-pass",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyEntryPass(@RequestParam("file") MultipartFile file) {

        try {
            String token = qrCodeService.readQRCode(file);
            return
                    new ResponseEntity<>(hostService.verifyEntryPass(token), HttpStatus.OK);
        } catch (IOException e) {
            var response = ApiResponse.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile image) {
            String imageUrl = hostService.uploadImage(image);
            return new ResponseEntity<>(imageUrl, HttpStatus.OK);
    }
}
