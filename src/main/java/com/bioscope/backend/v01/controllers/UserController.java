package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.models.ApiResponse;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.ReviewModel;
import com.bioscope.backend.v01.models.host.SeatingArrangementModel;
import com.bioscope.backend.v01.models.host.ShowModel;
import com.bioscope.backend.v01.models.user.PaymentVerificationRequest;
import com.bioscope.backend.v01.models.user.SearchResult;
import com.bioscope.backend.v01.models.user.TicketModel;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.services.iface.BookingService;
import com.bioscope.backend.v01.services.iface.HostService;
import com.bioscope.backend.v01.services.iface.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v01/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final HostService hostService;

    public UserController(UserService userService,
                          BookingService bookingService,
                          HostService hostService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.hostService = hostService;
    }

    @GetMapping(value = "/trending-shows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShowModel>> trendingShows(@RequestParam String location) {
        return new ResponseEntity<>(userService.trendingShows(location), HttpStatus.OK);
    }

    @GetMapping(value = "/trending-movies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieModel>> trendingMovies() {
        return new ResponseEntity<>(userService.trendingMovies(), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserModel> getUserProfile() {
        return new ResponseEntity<>(userService.getUserProfile(), HttpStatus.OK);
    }

    @GetMapping(value = "/streaming/movies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieModel>> currentlyStreamingMovies(
            @RequestParam String location) {
        return
                new ResponseEntity<>(userService.currentlyStreamingMovies(location),
                        HttpStatus.OK);
    }

    @GetMapping(value = "/hosts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserModel>> getHostsByLocation(
            @RequestParam String location,
            @RequestParam String movieName) {

        List<UserModel> hosts;
        if (movieName == null) {
            hosts = userService.getHostsByLocation(location);
        }
        else {
            hosts = userService.getHostsByMovieAndLocation(movieName, location);
        }

        return new ResponseEntity<>(hosts, HttpStatus.OK);
    }

    @GetMapping(value = "/shows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ShowModel>> getShowsByMovie(
            @RequestParam String hostId,
            @RequestParam String movieName) {
        return new ResponseEntity<>(userService.showsByHostWithMovie(hostId, movieName), HttpStatus.OK);
    }

    @GetMapping(value = "/shows/seating", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SeatingArrangementModel> getSeatingArrangement(
            @RequestParam String showId) {
        return new ResponseEntity<>(userService.getSeatingArrangement(showId), HttpStatus.OK);
    }

    @PostMapping(value = "/review/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewModel> addReview(
            @PathVariable String movieId,
            @RequestBody ReviewModel reviewModel) {
        return new ResponseEntity<>(userService
                .addReview(movieId, reviewModel), HttpStatus.OK);
    }

    @GetMapping(value = "/movies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieModel>> getMoviesByGenre(
            @RequestParam String genre) {
        return new ResponseEntity<>(userService.getMoviesByGenre(genre), HttpStatus.OK);
    }

    @GetMapping(value = "/movie/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieModel> getMovieById(
            @PathVariable String movieId) {
        return new ResponseEntity<>(hostService.getMovie(movieId), HttpStatus.OK);
    }
    @GetMapping(value = "/show/{showId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowModel> getShowById(
            @PathVariable String showId) {
        return new ResponseEntity<>(hostService.getShow(showId), HttpStatus.OK);
    }
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResult> searchAnything(
            @RequestParam String query,
            @RequestParam String location) {
        return new ResponseEntity<>(userService.searchAnything(query, location), HttpStatus.OK);
    }

    @PostMapping(value = "/booking/{showId}", consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketModel> bookTicket(
            @PathVariable String showId,
            @RequestParam Double amount,
            @RequestBody List<String> showSeatId
    ){
        log.info(showSeatId.toString());
        return new ResponseEntity<>(bookingService.bookSeats(showId, showSeatId, amount), HttpStatus.OK);
    }

    @PostMapping(value = "/booking/pass/{showId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketModel> bookEntryPass(
            @RequestParam String category,
            @RequestParam Integer quantity,
            @RequestParam Double amount,
            @PathVariable String showId
    ){
        TicketModel ticket = bookingService.bookEntryPass(showId, category, quantity, amount);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    @PostMapping(value = "/verify-payment", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest verificationRequest){
        if(bookingService.verifyPayment(verificationRequest)){
            return new ResponseEntity<>(ApiResponse.builder()
                    .status(true)
                    .message("Payment verified")
                    .build(), HttpStatus.OK);
        }else{
        return new ResponseEntity<>(ApiResponse.builder()
                .status(false)
                .message("Payment verification failed")
                .build(), HttpStatus.BAD_REQUEST);
    }
        }

    @PostMapping(value = "/booking/cancel/{showId}/{ticketId}")
    public ResponseEntity<ApiResponse> cancelTickets(
            @PathVariable String showId,
            @PathVariable String ticketId,
            @RequestBody List<String> showSeatId
    ) {
        bookingService.cancelBooking(showId, showSeatId, ticketId);
        var response = ApiResponse.builder()
                .status(true)
                .message("Ticket cancelled")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/pass/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> cancelPass(
            @RequestBody TicketModel ticket
    ) {
        bookingService.cancelEntryPass(ticket);
        var response = ApiResponse.builder()
                .message("Pass Canceled")
                .status(true)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
