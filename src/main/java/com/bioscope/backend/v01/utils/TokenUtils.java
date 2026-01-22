package com.bioscope.backend.v01.utils;


import com.bioscope.backend.v01.entities.ActionTokenEntity;
import com.bioscope.backend.v01.entities.RefreshTokenEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.enums.ActionType;
import com.bioscope.backend.v01.repos.ActionTokenRepo;
import com.bioscope.backend.v01.repos.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenUtils {


    @Value("${opaque.secret}")
    private  String SECRET;

    private final RefreshTokenRepo refreshTokenRepo;
    private final ActionTokenRepo actionTokenRepo;

    public byte[] generateOpaqueBytes() {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public byte[] hashActionToken(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to encode token with sha256");
        }
    }

    public Optional<ActionTokenEntity> validateAndFindActionToken(String input) {
        byte[] decoded = Base64.getUrlDecoder().decode(input);
        byte[] hashed = hashActionToken(decoded);

        return actionTokenRepo.findByHashedTokenAndUsedFalseAndExpiresAtAfter(Hex.encodeHexString(hashed),
                Instant.now());
    }

    public byte[] hashRefreshToken(byte[] bytes) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(), "HmacSHA256"));
            return mac.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode token with HMAC-SHA256");
        }
    }

    public Optional<RefreshTokenEntity> validateAndFindRefresh(String input) {
        byte[] decoded =
                Base64.getUrlDecoder().decode(input);
        byte [] hashed = hashRefreshToken(decoded);

        return
                refreshTokenRepo
                        .findRefreshTokenEntityByTokenHash(Hex.encodeHexString(hashed));

    }

    public String generateActionTokenForUser(String email, ActionType actionType, Instant expiry) {
        byte [] tokenBytes = generateOpaqueBytes();
        ActionTokenEntity actionToken =
                new ActionTokenEntity();
        actionToken.setEmail(email);
        actionToken.setActionType(actionType);
        actionToken.setUsed(false);
        actionToken.setHashedToken(Hex
                .encodeHexString(hashActionToken(tokenBytes)));
        actionToken.setCreatedAt(Instant.now());
        actionToken.setExpiresAt(expiry);
        actionTokenRepo.save(actionToken);
        return
                Base64.getUrlEncoder().encodeToString(tokenBytes);
    }


}
