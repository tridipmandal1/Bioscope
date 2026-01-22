package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.RefreshTokenEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.enums.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity, UUID> {


    Optional<RefreshTokenEntity> findRefreshTokenEntityByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findByUserAndRevokedFalseAndExpiresAtAfter(
            UserEntity user, Instant now);

    @Query("SELECT t FROM RefreshTokenEntity t " +
            "WHERE t.user = :user " +
            "AND t.revoked = false " +
            "AND t.expiresAt > :now")
    List<RefreshTokenEntity> findActiveTokensByUser(@Param("user") UserEntity user, @Param("now") Instant now);


    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity t SET t.revoked = true " +
            "WHERE t.user = :user AND t.revoked = false " +
            "AND t.expiresAt > :now")
    int revokeActiveTokensByUser(@Param("user") UserEntity user, @Param("now") Instant now);

    Optional<RefreshTokenEntity> findByUserAndClientTypeAndRevokedFalseAndExpiresAtAfter(
            UserEntity user, ClientType clientType, Instant now);
}
