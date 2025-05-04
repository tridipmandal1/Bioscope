package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.VarcharUUIDJdbcType;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "screens")
@Getter
@Setter
public class ScreenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcType(VarcharUUIDJdbcType.class)
    @Column(columnDefinition = "CHAR(36)")
    private UUID screenId;

    private String screenName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity userEntity;


    @OneToOne(mappedBy = "screen", cascade = CascadeType.ALL)
    private SeatingArrangementEntity seatingArrangement;


    @OneToMany(mappedBy = "screen")
    private List<ShowEntity> shows;

    public void addShow(ShowEntity show){
        shows.add(show);
        show.setScreen(this);
    }
    public void removeShow(ShowEntity show){
        this.shows.remove(show);
    }

}
