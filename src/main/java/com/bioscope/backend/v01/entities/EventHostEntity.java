package com.bioscope.backend.v01.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "hosts")
public class EventHostEntity {

    @Id
    private UUID host_id;

    private String name;

    @Email
    private String email;

    private String password;

    private  String location;

    @OneToMany(mappedBy = "eventHost")
    private List<ScreenEntity> screens;

}
