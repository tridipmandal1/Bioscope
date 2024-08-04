package com.bioscope.backend.v01.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class EventHostEntity {

    @Id
    private UUID host_id;

    private String host_name;

    @Email
    private String host_email;

    private String password;

    private  String location;

    @OneToMany
    private List<ShowEntity> shows;

    @OneToMany
    private List<ScreenEntity> screens;


}
