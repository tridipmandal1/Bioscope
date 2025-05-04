package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.*;
import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.*;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.ReviewModel;
import com.bioscope.backend.v01.models.host.SeatingArrangementModel;
import com.bioscope.backend.v01.models.host.ShowModel;
import com.bioscope.backend.v01.models.user.SearchResult;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.repos.*;
import com.bioscope.backend.v01.services.iface.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserMapper userMapper;
    private final ShowMapper showMapper;
    private final MovieMapper movieMapper;
    private final SeatingArrangementMapper seatingArrangementMapper;
    private final ReviewMapper reviewMapper;
    private final ShowSeatMapper showSeatMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            ShowRepository showRepository,
            MovieRepository movieRepository,
            GenreRepository  genreRepository,
            UserMapper userMapper,
            ShowMapper showMapper,
            MovieMapper movieMapper,
            SeatingArrangementMapper seatingArrangementMapper,
            ReviewMapper reviewMapper,
            ShowSeatMapper showSeatMapper
           ){
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.userMapper = userMapper;
        this.showMapper = showMapper;
        this.movieMapper = movieMapper;
        this.seatingArrangementMapper = seatingArrangementMapper;
        this.reviewMapper = reviewMapper;
        this.showSeatMapper = showSeatMapper;
    }

    @Override
    public List<ShowModel> trendingShows(@RequestParam String location) {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }

        List<ShowEntity> shows = showRepository.findTrendingShowsByLocation(location);

        if (shows.isEmpty()) {
            throw new ResourceNotFoundException("No shows are currently trending in your location");
        }

        List<ShowEntity>  openShows =
                shows.stream().filter(show -> show.getMovie() == null).toList();
        if (openShows.isEmpty()) {
            throw new ResourceNotFoundException("No shows are currently trending in your location");
        }
        return openShows.stream().map(showMapper::entityToModel).toList();
    }

    @Override
    public List<MovieModel> trendingMovies() {
        List<MovieEntity> movies = movieRepository.trendingMovies();
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies are currently trending");
        }
        return movies.stream().map(movieMapper::entityToModel).toList();
    }

    @Override
    public UserModel getUserProfile() {
        UserEntity user = this.getUserContext();
        return userMapper.entityToModel(user);
    }

    @Override
    public List<MovieModel> currentlyStreamingMovies(String location) {
        List<ShowEntity> shows =
                showRepository.findShowEntitiesByLocation(location);
        if (!shows.isEmpty()) {
          return  shows.parallelStream()
                    .map(ShowEntity::getMovie)
                    .distinct()
                    .map(movieMapper::entityToModel)
                    .toList();
        }
        throw new ResourceNotFoundException("No movies are currently streaming in your location");
    }

    @Override
    public List<UserModel> getHostsByMovieAndLocation(String movieName, String location) {
        List<UserEntity> users =
                userRepository.findUserEntitiesByLocationAndRole(location, Roles.HOST);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No Hosts found in your location");
        }
        Predicate<UserEntity> forMovie = user -> user
                .getMovieShows()
                .stream()
                .anyMatch(show -> show
                        .getMovie().getTitle().equals(movieName));
        Predicate<UserEntity> forDate = user -> user
                .getShows().stream().anyMatch(show ->
                        show.getShowDate()
                                .isBefore(LocalDate.now().plus(Period.ofWeeks(1))));
      List<UserEntity> hosts =   users.stream().filter(forMovie.and(forDate))
                .toList();

        hosts.forEach(host -> {});

        return hosts.stream().map(userMapper::entityToModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserModel> getHostsByLocation(String location) {
        List<UserEntity> hosts =  userRepository.findUserEntitiesByLocation(location);
        if (hosts.isEmpty()) {
            throw new ResourceNotFoundException("No Hosts found in your location");
        }
        return hosts.stream().map(userMapper::entityToModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShowModel> showsByHostWithMovie(String hostId, String movieName) {
        UserEntity host = userRepository.findById(UUID.fromString(hostId)).orElseThrow(
                () -> new ResourceNotFoundException("Host", "HostId", hostId)
        );
        List<ShowEntity> shows = host.getMovieShows().stream()
                .filter(show -> show.getMovie().getTitle().equals(movieName))
                .toList();

        return shows.stream().map(showMapper::entityToModel).toList();
    }

    @Override
    public SeatingArrangementModel getSeatingArrangement(String showId) {
        ShowEntity show = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                ()-> new ResourceNotFoundException("Show", "ShowId", showId)
        );

        SeatingArrangementModel model = seatingArrangementMapper
                .entityToModel(show.getScreen().getSeatingArrangement());
        model.setShowSeats(show.getShowSeats()
                .stream().map(showSeatMapper::entityToModel).toList());
        return model;
    }

    @Override
    public MovieModel addReview(String movieId, ReviewModel reviewModel) {
        if (movieId == null || reviewModel == null) {
            throw new IllegalArgumentException("MovieId and ReviewModel must not be null");
        }
        MovieEntity movie = movieRepository.findById(UUID.fromString(movieId)).orElseThrow(
                ()-> new ResourceNotFoundException("Movie", "MovieId", movieId)
        );
        UserEntity user = this.getUserContext();
        ReviewEntity entity = reviewMapper.modelToEntity(reviewModel);
        entity.setMovie(movie);
        entity.setUser(user);
        List<ReviewEntity> reviews = movie.getReviews();
        reviews.add(entity);
        movie.setReviews(reviews);
        List<ReviewEntity> userReviews = user.getReviews();
        userReviews.add(entity);
        user.setReviews(userReviews);
        userRepository.save(user);
        movieRepository.save(movie);
        return movieMapper.entityToModel(movie);
    }

    @Override
    public List<MovieModel> getMoviesByGenre(String genre) {
        GenreEntity genreEntity = genreRepository.findByGenreName(genre).orElseThrow(
                ()-> new ResourceNotFoundException("Genre", "GenreName", genre)
        );
        return genreEntity.getMovies().stream().map(movieMapper::entityToModel).toList();
    }

    @Override
    public SearchResult searchAnything(String query, String location) {
        if (query == null || location == null) {
            throw new IllegalArgumentException("Query must not be null");
        }
        List<ShowEntity> shows = showRepository.findShowEntitiesByQuery(query, location);
        List<UserEntity> hosts = userRepository.searchHosts(query, Roles.HOST);
        List<MovieEntity> movies = movieRepository.searchMovies(query);
        if (shows.isEmpty() && hosts.isEmpty() && movies.isEmpty()) {
            return SearchResult.builder()
                    .message("No results found")
                    .build();
        }
        List<ShowModel> showModels = shows.stream()
                .map(showMapper::entityToModel)
                .toList();
        List<UserModel> hostModels = hosts.stream()
                .map(userMapper::entityToModel)
                .toList();
        List<MovieModel> movieModels = movies.stream()
                .map(movieMapper::entityToModel)
                .toList();

        return SearchResult.builder()
                .shows(showModels)
                .hosts(hostModels)
                .movies(movieModels)
                .message("Search results for: " + query)
                .build();
    }

    private UserEntity getUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }
        String username;
        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal);
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Failed to fetch user: " + username));

    }
}
