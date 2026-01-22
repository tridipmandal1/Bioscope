package com.bioscope.backend.v01.repos;

import com.bioscope.backend.v01.entities.ActionTokenEntity;
import com.bioscope.backend.v01.enums.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ActionTokenRepo extends JpaRepository<ActionTokenEntity, UUID> {

    Optional<ActionTokenEntity> findByHashedTokenAndUsedFalseAndExpiresAtAfter(
            String hashedToken, Instant now);

    Optional<ActionTokenEntity> findByEmailAndActionTypeAndUsedFalseAndExpiresAtAfter(
            String email,
            ActionType actionType,
            Instant now);


    default Optional<ActionTokenEntity> findValidEmailVerificationToken(String email, Instant now) {
        return findByEmailAndActionTypeAndUsedFalseAndExpiresAtAfter(email, ActionType.EMAIL_VERIFICATION, now);
    }

    default Optional<ActionTokenEntity> findValidPasswordResetToken(String email, Instant now) {
        return findByEmailAndActionTypeAndUsedFalseAndExpiresAtAfter(email, ActionType.PASSWORD_RESET, now);
    }

    default Optional<ActionTokenEntity> findValidChangePasswordToken(String email, Instant now) {
        return findByEmailAndActionTypeAndUsedFalseAndExpiresAtAfter(email, ActionType.CHANGE_PASSWORD, now);
    }

    default Optional<ActionTokenEntity> findValidTicketPassToken(String email, Instant now) {
        return findByEmailAndActionTypeAndUsedFalseAndExpiresAtAfter(email, ActionType.TICKET_PASS, now);
    }


    List<ActionTokenEntity> findByEmailAndUsedFalseAndExpiresAtAfter(
            String email,
            Instant now);

    @Query("SELECT t FROM ActionTokenEntity t " +
            "WHERE t.email = :email " +
            "AND t.used = false " +
            "AND t.expiresAt > :now " +
            "ORDER BY t.createdAt DESC")
    List<ActionTokenEntity> findAllValidTokensByEmail(
            @Param("email") String email,
            @Param("now") Instant now);



    @Modifying
    @Transactional
    @Query("UPDATE ActionTokenEntity t SET t.used = true WHERE t.id = :id")
    int markAsUsed(@Param("id") UUID id);


    @Modifying
    @Transactional
    @Query("UPDATE ActionTokenEntity t SET t.used = true " +
            "WHERE t.email = :email AND t.actionType = :actionType")
    int invalidateAllByEmailAndType(
            @Param("email") String email,
            @Param("actionType") ActionType actionType);
}
