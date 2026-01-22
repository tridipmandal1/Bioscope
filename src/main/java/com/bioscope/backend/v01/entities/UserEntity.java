package com.bioscope.backend.v01.entities;

import com.bioscope.backend.v01.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;


    private String email;

    private String password;

    private Roles role;

    private String name;


    private String location;

    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<GenreEntity> interests = new ArrayList<>();

    @ManyToMany( fetch = FetchType.LAZY)
    @JoinTable(
            name = "watched_movies",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<MovieEntity> watchedMovies;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowEntity> shows = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<ScreenEntity> screens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketEntity> bookedTickets;

    @OneToMany(mappedBy = "user")
    private List<RefreshTokenEntity> refreshTokens;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PaymentHistoryEntity> paymentHistory;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public void addTicket(TicketEntity ticket) {
        this.bookedTickets.add(ticket);
    }

    public void addScreen(ScreenEntity screen) {
        this.screens.add(screen);
    }

    public List<ShowEntity> getMovieShows() {
        return shows
                .stream().filter(show -> show.getMovie() != null).toList();
    }

    public void addShow(ShowEntity show) {
        this.shows.add(show);
        show.setUser(this);
    }
}
