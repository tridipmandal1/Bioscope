package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.constants.SeatId;
import com.bioscope.backend.v01.entities.*;
import com.bioscope.backend.v01.enums.ArrangementType;
import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.enums.SeatCategory;
import com.bioscope.backend.v01.enums.SeatStatus;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.*;
import com.bioscope.backend.v01.models.MovieModel;
import com.bioscope.backend.v01.models.PassCategoryModel;
import com.bioscope.backend.v01.models.SeatViewModel;
import com.bioscope.backend.v01.models.host.*;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.repos.*;
import com.bioscope.backend.v01.services.iface.BucketService;
import com.bioscope.backend.v01.services.iface.HostService;
import com.bioscope.backend.v01.utils.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class HostServiceImpl implements HostService {

    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final SeatArrangementRepository seatArrangementRepository;
    private final SeatRowRepository seatRowRepository;
    private final ScreenMapper screenMapper;
    private final UserMapper userMapper;
    private final ShowMapper showMapper;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final GenreRepository genreRepository;
    private final TicketRepository ticketRepository;
    private final EncryptionUtil encryptionUtil;
    private final SeatingArrangementMapper seatingArrangementMapper;
    private final PassCategoryMapper passCategoryMapper;
    private final PassCategoryRepository passCategoryRepository;
    private final BucketService bucketService;


    public HostServiceImpl(
            UserRepository userRepository,
            ShowRepository showRepository,
            ScreenRepository screenRepository,
            ScreenMapper screenMapper,
            SeatArrangementRepository seatArrangementRepository,
            SeatRowRepository seatRowRepository,
            UserMapper userMapper, ShowMapper showMapper,
            MovieRepository movieRepository,
            MovieMapper movieMapper,
            GenreRepository genreRepository,
            TicketRepository ticketRepository,
            EncryptionUtil encryptionUtil, SeatingArrangementMapper seatingArrangementMapper, PassCategoryMapper passCategoryMapper, PassCategoryRepository passCategoryRepository, BucketService bucketService){
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.screenRepository = screenRepository;
        this.screenMapper = screenMapper;
        this.seatArrangementRepository = seatArrangementRepository;
        this.seatRowRepository = seatRowRepository;
        this.userMapper = userMapper;
        this.showMapper = showMapper;
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.genreRepository = genreRepository;
        this.ticketRepository = ticketRepository;
        this.encryptionUtil = encryptionUtil;
        this.seatingArrangementMapper = seatingArrangementMapper;
        this.passCategoryMapper = passCategoryMapper;
        this.passCategoryRepository = passCategoryRepository;
        this.bucketService = bucketService;
    }

    @Override
    public UserModel getHostProfile() {
        UserEntity user = this.getUserContext();
        return userMapper.entityToModel(user);
    }

    @Override
    @Transactional
    public ScreenModel createScreen(ScreenRequestModel requestModel) {
        if (requestModel == null) {
            throw new RuntimeException("Request model is null");
        }

        UserEntity user = this.getUserContext();
        ScreenEntity screenEntity = new ScreenEntity();
        SeatingArrangementEntity arrangementEntity = new SeatingArrangementEntity();

        screenEntity.setScreenName(requestModel.getScreenName());
        screenEntity.setUserEntity(user);
        screenRepository.save(screenEntity);

        arrangementEntity.setArrangementType(ArrangementType.valueOf(requestModel.getArrangementType()));
        arrangementEntity.setCapacity(requestModel.getCapacity());
        arrangementEntity.setScreen(screenEntity);
        seatArrangementRepository.save(arrangementEntity);

        List<SeatRowEntity> rows = new ArrayList<>();
        requestModel.getRowData().forEach(
                rowData -> {
                    SeatRowEntity seatRowEntity = new SeatRowEntity();
                    seatRowEntity.setSeatingArrangement(arrangementEntity);
                    seatRowEntity.setSeatCategory(SeatCategory.valueOf(rowData.getCategory()));
                    seatRowEntity.setRowIndex(rowData.getRowIndex());
                    seatRowEntity.setPassageAfterwards(rowData.getPassageFollowed());

                    // Populate seats using the existing collection
                    for (int k = 1; k <= rowData.getSeatsInRow(); k++) {
                        SeatEntity seatEntity = new SeatEntity();
                        seatEntity.setId(new SeatId(
                                arrangementEntity.getArrangementId(),
                                rowData.getRowIndex(),
                                k));
                        seatEntity.setPrice(rowData.getPriceInRow());
                        seatRowEntity.addSeat(seatEntity); // Use addSeat to maintain relationship
                    }
                    rows.add(seatRowEntity);
                }
        );

        seatRowRepository.saveAll(rows); // Save all rows with their seats
        arrangementEntity.setSeatRows(rows);
        seatArrangementRepository.save(arrangementEntity);
        screenEntity.setSeatingArrangement(arrangementEntity);
        screenRepository.save(screenEntity);

        return screenMapper.entityToModel(screenEntity);
    }

    @Override
    public ScreenModel getScreen(String screenId) {
        if (screenId == null) {
            throw new RuntimeException("Screen id is null");
        }
        ScreenEntity screen = screenRepository.findById(UUID.fromString(screenId)).orElseThrow(
                () -> new ResourceNotFoundException("Screen", "id", screenId)
        );
        return screenMapper.entityToModel(screen);
    }

    @Override
    @Transactional
    public ScreenModel updateScreenName(String screenId, String newName) {
        if (screenId == null) {
            throw new RuntimeException("Screen id is null");
        }

        ScreenEntity screenEntity =
                screenRepository.findById(UUID.fromString(screenId)).orElseThrow(
                        () -> new ResourceNotFoundException("Screen", "id", screenId)
                );
        screenEntity.setScreenName(newName);
        return screenMapper.entityToModel(screenRepository.save(screenEntity));
    }

    @Override
    public SeatingArrangementModel getSeatingArrangement(String arrangementId) {
        if (arrangementId == null) {
            throw new IllegalArgumentException("Arrangement id is null");
        }

        SeatingArrangementEntity entity =
                seatArrangementRepository.findById(UUID.fromString(arrangementId)).orElseThrow(
                        () ->    new ResourceNotFoundException("SeatingArrangement", "id", arrangementId)
                );
        return seatingArrangementMapper.entityToModel(entity);
    }

    @Override
    public ScreenModel updateScreenSeatArrangement(String arrangementId, ScreenRequestModel requestModel) {
        if (arrangementId == null || requestModel == null) {
            throw new RuntimeException("Screen id or request model is null");
        }

        SeatingArrangementEntity arrangementEntity = seatArrangementRepository.findById(UUID.fromString(arrangementId))
                .orElseThrow(() -> new ResourceNotFoundException("SeatingArrangement", "id", arrangementId));

        arrangementEntity.setCapacity(requestModel.getCapacity());
        arrangementEntity.setArrangementType(ArrangementType.valueOf(requestModel.getArrangementType()));

        // Work with the existing seatRows collection
        AtomicInteger k = new AtomicInteger(0);
        arrangementEntity.getSeatRows().replaceAll(seatRowEntity ->
                this.modifyOneRow(arrangementId,
                        seatRowEntity.getRowId().toString(),
                        requestModel.getRowData().get(k.getAndIncrement()))
        );

        seatRowRepository.saveAll(arrangementEntity.getSeatRows()); // Save the modified rows
        seatArrangementRepository.save(arrangementEntity); // Save the updated arrangement
        return screenMapper.entityToModel(arrangementEntity.getScreen());
    }

    private SeatRowEntity modifyOneRow(String arrangementId, String rowUuid, RowData rowData) {
        SeatRowEntity rowEntity = seatRowRepository.findById(UUID.fromString(rowUuid)).orElseThrow(
                () -> new ResourceNotFoundException("SeatRow", "id", rowUuid)
        );
        rowEntity.setPassageAfterwards(rowData.getPassageFollowed());
        rowEntity.setRowIndex(rowData.getRowIndex());
        rowEntity.setSeatCategory(SeatCategory.valueOf(rowData.getCategory()));

        if (rowEntity.getSeats().size() != rowData.getSeatsInRow()) {
            rowEntity.getSeats().clear();

            int k = 1;
            while (k <= rowData.getSeatsInRow()) {
                SeatEntity seatEntity = new SeatEntity();
                seatEntity.setId(new SeatId(
                        UUID.fromString(arrangementId),
                        rowData.getRowIndex(),
                        k
                ));
                seatEntity.setSeatRowEntity(rowEntity);
                seatEntity.setPrice(rowData.getPriceInRow());
                rowEntity.getSeats().add(seatEntity);
                k++;
            }
        }

        if (rowEntity.getSeats().size() == rowData.getSeatsInRow() &&
                !Objects.equals(rowData.getPriceInRow(), rowEntity.getSeats().get(0).getPrice())) {
            rowEntity.getSeats().forEach(seatEntity ->
                    seatEntity.setPrice(rowData.getPriceInRow()));
        }

        return rowEntity;
    }


    @Override
    @Transactional
    public SeatingArrangementModel updateSeatArrangementByOneRow(String arrangementId, RowData rowData){
        if (arrangementId == null || rowData == null) {
            throw new RuntimeException("Screen id or row data is null");
        }

        SeatingArrangementEntity arrangementEntity =
                seatArrangementRepository.findById(UUID.fromString(arrangementId)).orElseThrow(
                        () -> new ResourceNotFoundException("Arrangement", "id", arrangementId)
                );

                arrangementEntity.getSeatRows().forEach(
                    seatRowEntity -> {
                        if (seatRowEntity.getRowIndex().equals(rowData.getRowIndex())) {
                            String rowUuid = seatRowEntity.getRowId().toString();
                            SeatRowEntity rowEntity = modifyOneRow(
                                    arrangementEntity.getArrangementId().toString(),
                                    rowUuid,
                                    rowData
                            );
                            seatRowRepository.save(rowEntity);
                        }
                    }
                );
        return seatingArrangementMapper.entityToModel(arrangementEntity);
    }

    @Override
    public List<ScreenModel> getAllScreens() {
        UserEntity user = this.getUserContext();
        List<ScreenEntity> screenEntities = screenRepository.findScreenEntitiesByUserEntity(user);
        if (screenEntities.isEmpty()) {
            throw new ResourceNotFoundException("No screens found");
        }
        return screenEntities.stream().map(screenMapper::entityToModel).toList();
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteScreen(String screenId) {
        if (screenId == null) {
            throw new RuntimeException("Screen id is null");
        }
        ScreenEntity screen = screenRepository.findById(UUID.fromString(screenId)).orElseThrow(
                () -> new ResourceNotFoundException("Screen", "id", screenId)
        );
        screenRepository.delete(screen);
    }

    @Override
    @Transactional
    public ShowModel createShow(String screenId, ShowModel showModel) {
        UserEntity host = this.getUserContext();
        ScreenEntity screen = screenRepository.findById(UUID.fromString(screenId)).orElseThrow(
                () -> new ResourceNotFoundException("Screen", "id", screenId)
        );

        ShowEntity showEntity = showMapper.modelToEntity(showModel);
        showEntity.setMovie(null);

        if (showModel.getMovie() != null && showModel.getMovie().getMovieId() != null) {
            MovieEntity movieEntity = movieRepository
                    .findById(UUID.fromString(showModel.getMovie().getMovieId())).orElseThrow(
                            () -> new ResourceNotFoundException("Movie", "id", showModel.getMovie().getMovieId())
                    );
            showEntity.setMovie(movieEntity);
        }

        List<ShowSeatEntity> showSeats = new ArrayList<>();
        screen.getSeatingArrangement().getSeatRows().forEach(
                seatRowEntity -> seatRowEntity.getSeats().forEach(
                        seatEntity -> {
                            ShowSeatEntity showSeatEntity = new ShowSeatEntity();
                            showSeatEntity.setSeat(seatEntity);
                            showSeatEntity.setSeatStatus(SeatStatus.AVAILABLE);
                            showSeatEntity.setShow(showEntity);
                            showSeats.add(showSeatEntity);
                            seatEntity.addShowSeat(showSeatEntity); // Maintain bidirectional relationship
                        }
                )
        );
        showEntity.setShowSeats(showSeats);

        showEntity.setScreen(screen);
        screen.addShow(showEntity);
        showEntity.setUser(host);
        host.getShows().add(showEntity);

        showRepository.save(showEntity);

        return showMapper.entityToModel(showEntity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ShowModel createOpenShow(ShowModel showModel) {
        if (showModel == null) {
            throw new RuntimeException("Show model is null");
        }

        showModel.setMovie(null);
        UserEntity host = this.getUserContext();
        ShowEntity showEntity = showMapper.modelToEntity(showModel);
        List<PassCategoryEntity> categoryEntities =
                showEntity.getTicketPrice();
        categoryEntities.forEach(pass -> pass.setShow(showEntity));
        passCategoryRepository.saveAll(categoryEntities);
        showEntity.setUser(host);
        showRepository.save(showEntity);
        return showMapper.entityToModel(showEntity);
    }

    @Override
    public ShowModel updateOpenShow(String showId, ShowModel showModel) {
        if (showId == null || showModel == null) {
            throw new RuntimeException("Show id or model is null");
        }

        ShowEntity showEntity = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                () -> new ResourceNotFoundException("Show", "id", showId)
        );

        showEntity.setShowName(showModel.getShowName());
        showEntity.setShowType(showModel.getShowType());
        showEntity.setPoster(showModel.getPoster());
        showEntity.setLocation(showModel.getLocation());
        showEntity.setCapacity(showModel.getCapacity());
        showEntity.setShowDescription(showModel.getShowDescription());
        showEntity.setReserved(showModel.getReserved());
        showEntity.setShowDate(LocalDate.parse(showModel.getShowDate()));
        showEntity.setShowTime(LocalTime.parse(showModel.getShowTime()));
        showEntity.setShowDuration(Duration.parse(showModel.getShowDuration()));
        if (showModel.getTicketPrice() != null) {
            List<PassCategoryEntity> passes =
                    showModel.getTicketPrice()
                            .stream()
                            .map(passCategoryMapper::modelToEntity)
                                    .toList();
            showEntity.getTicketPrice().clear();
            showEntity.getTicketPrice().addAll(passes);
        }
        showEntity.setBookings(showEntity.getBookings());

        showRepository.save(showEntity);
        return showMapper.entityToModel(showEntity);
    }

    @Override
    public ShowModel getShow(String showId) {
        if (showId == null) {
            throw new RuntimeException("Show id is null");
        }

        ShowEntity showEntity =
                showRepository.findById(UUID.fromString(showId)).orElseThrow(
                        () -> new ResourceNotFoundException("Show", "id", showId)
                );
        return showMapper.entityToModel(showEntity);
    }

    @Override
    @Transactional
    public ShowModel updateShow(String showId, ShowModel showModel) {

        if (showId == null || showModel == null) {
            throw new RuntimeException("Show id or model is null");
        }

        ShowEntity showEntity = showRepository.findById(UUID.fromString(showId)).orElseThrow(
                () -> new ResourceNotFoundException("Show", "id", showId)
        );

        ScreenEntity pastScreen = showEntity.getScreen();
        pastScreen.removeShow(showEntity);
        showEntity.setScreen(null);
        screenRepository.save(pastScreen);

        showEntity.setShowName(showModel.getShowName());
        showEntity.setShowType(showModel.getShowType());
        showEntity.setPoster(showModel.getPoster());
        showEntity.setLocation(showModel.getLocation());
        showEntity.setShowDescription(showModel.getShowDescription());
        showEntity.setShowDate(LocalDate.parse(showModel.getShowDate()));
        showEntity.setShowTime(LocalTime.parse(showModel.getShowTime()));
        showEntity.setShowDuration(Duration.parse(showModel.getShowDuration()));
        showEntity.setCapacity(showModel.getCapacity());
        if (showModel.getTicketPrice() != null) {
            List<PassCategoryEntity> passes =
                    showModel.getTicketPrice()
                            .stream()
                            .map(passCategoryMapper::modelToEntity)
                            .toList();
            showEntity.getTicketPrice().clear();
            showEntity.getTicketPrice().addAll(passes);
        }
        if (showModel.getMovie() != null && showModel.getMovie().getMovieId() != null) {
            MovieEntity movieEntity = movieRepository
                    .findById(UUID.fromString(showModel.getMovie().getMovieId())).orElseThrow(
                            () -> new ResourceNotFoundException("Movie", "id", showModel.getMovie().getMovieId())
                    );
            showEntity.setMovie(movieEntity);
        }
        if (showModel.getScreenId() != null) {
            ScreenEntity screenEntity = screenRepository
                    .findById(UUID.fromString(showModel.getScreenId())).orElseThrow(
                            () -> new ResourceNotFoundException("Screen", "id", showModel.getScreenId())
                    );
            showEntity.setScreen(screenEntity);
            screenEntity.addShow(showEntity);
            screenRepository.save(screenEntity);
        }
        showRepository.save(showEntity);
        return showMapper.entityToModel(showEntity);
    }

    @Override
    public List<ShowModel> getAllShows() {
        UserEntity user = this.getUserContext();
        List<ShowEntity> shows = showRepository.findShowEntitiesByUser(user);
        if (shows.isEmpty()) {
            throw new ResourceNotFoundException("No shows found");
        }
        List<ShowEntity> showEntis= shows.stream()
                .filter(show -> show.getShowDate().isAfter(LocalDate.now()))
                .toList();
        return showEntis.stream().map(showMapper::entityToModel).toList();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteShow(String showId) {
        if (showId == null) {
            throw new RuntimeException("Show id is null");
        }
        showRepository.deleteById(UUID.fromString(showId));
    }

    @Override
    public List<MovieModel> getAllMovies() {
        List<MovieEntity> movies = movieRepository.findAll();
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found");
        }
        return movies.stream().map(movieMapper::entityToModel).toList();
    }

    @Override
    public MovieModel getMovie(String movieId) {
        if (movieId == null) {
            throw new RuntimeException("Movie id is null");
        }
        MovieEntity movieEntity = movieRepository.findById(UUID.fromString(movieId)).orElseThrow(
                () -> new ResourceNotFoundException("Movie", "id", movieId)
        );
        return movieMapper.entityToModel(movieEntity);
    }



    @Override
    public List<SeatViewModel> verifyTicket(String token) {
        if (token == null) {
            throw new RuntimeException("Token is null");
        }
        UserEntity host = this.getUserContext();
        if (!host.getRole().equals(Roles.HOST)) {
            throw new RuntimeException("User is not a host");
        }
        String ticketId = encryptionUtil.decrypt(token);
        TicketEntity ticket = ticketRepository.findById(UUID.fromString(ticketId)).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "id", ticketId)
        );
        ShowEntity showEntity = showRepository.findById(UUID.fromString(ticket.getShowId())).orElseThrow(
                () -> new ResourceNotFoundException("Show", "id", ticket.getShowId())
        );
        if (!showEntity.getUser().getId().equals(host.getId())) {
            throw new RuntimeException("User is not the host of this show");
        }
        List<ShowSeatEntity> showSeats = ticket.getShowSeats();
        List<SeatViewModel> viewModels = new ArrayList<>();
        showSeats.forEach(showSeat -> {
            SeatViewModel svm = new SeatViewModel(
                    showEntity.getShowName(),
                    showEntity.getShowDate().toString(),
                    showEntity.getShowTime().toString(),
                    showSeat.getSeat().getId()
            );
            viewModels.add(svm);
        });

        return viewModels;
    }

    @Override
    public SeatViewModel verifyEntryPass(String token) {
        if (token == null) {
            throw new RuntimeException("Token is null");
        }
        UserEntity host = this.getUserContext();
        if (!host.getRole().equals(Roles.HOST)) {
            throw new RuntimeException("User is not a host");
        }

        String ticketId = encryptionUtil.decrypt(token);

        TicketEntity ticket = ticketRepository.findById(UUID.fromString(ticketId)).orElseThrow(
                () -> new ResourceNotFoundException("Ticket", "id", ticketId)
        );

        ShowEntity showEntity = showRepository.findById(UUID.fromString(ticket.getShowId())).orElseThrow(
                () -> new ResourceNotFoundException("Show", "id", ticket.getShowId())
        );

        if (!showEntity.getUser().getId().equals(host.getId())) {
            throw new RuntimeException("User is not the host of this show");
        }

        return new SeatViewModel(
                showEntity.getShowName(),
                showEntity.getShowDate().toString(),
                showEntity.getShowTime().toString(),
                ticket.getCategory(),
                ticket.getAllowedPersons()
        );
    }

    @Override
    public String uploadImage(MultipartFile image) {
        if(image == null || image.isEmpty()) {
            throw new RuntimeException("Image file is null or empty");
        }
        return bucketService.uploadFile(image);
    }

    private UserEntity getUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }
        String username;
        Object principal = authentication.getPrincipal();
        log.info("Principal: " + principal);
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Failed to fetch user: " + username));

    }


}
