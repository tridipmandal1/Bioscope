package com.bioscope.backend.v01.entities;


import com.bioscope.backend.v01.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharUUIDJdbcType;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "action_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    @JdbcType(VarcharUUIDJdbcType.class)
    private UUID id;


    @Column(nullable = false, unique = true)
    private String hashedToken;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private boolean used;

    private Instant createdAt;

    private Instant expiresAt;
}
