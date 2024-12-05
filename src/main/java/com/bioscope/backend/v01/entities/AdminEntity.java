package com.bioscope.backend.v01.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID adminId;

    private String username;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

    private  String role;


}
