package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.ActionTokenEntity;
import com.bioscope.backend.v01.entities.RefreshTokenEntity;
import com.bioscope.backend.v01.enums.ActionType;
import com.bioscope.backend.v01.enums.ClientType;
import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.exceptions.InvalidCredentialsException;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.exceptions.TokenCycleFailedException;
import com.bioscope.backend.v01.mapper.UserMapper;
import com.bioscope.backend.v01.models.EmailModel;
import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.repos.ActionTokenRepo;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.RefreshTokenRepo;
import com.bioscope.backend.v01.repos.UserRepository;
import com.bioscope.backend.v01.security.JwtProvider;
import com.bioscope.backend.v01.sender.EmailSender;
import com.bioscope.backend.v01.services.iface.AuthService;
import com.bioscope.backend.v01.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final ActionTokenRepo actionTokenRepo;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final GenreRepository genreRepository;
    private final EmailSender emailSender;
    private final TokenUtils tokenUtils;
    private final RefreshTokenRepo refreshTokenRepo;

    @Value("${api.uri}")
    private String api_url;

    @Value("${ui.uri}")
    private String ui_url;




    @Override
    public UserModel registerUser(UserRequestModel requestModel) {
        if (userRepository.findByEmail(requestModel.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User", "email", requestModel.getEmail());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(requestModel.getEmail());
        userEntity.setPassword(passwordEncoder.encode(requestModel.getPassword()));
        userEntity.setRole(Roles.valueOf(requestModel.getRole().toUpperCase()));
        userRepository.save(userEntity);
        sendVerificationEmail(requestModel.getEmail());
        return userMapper.entityToModel(userEntity);
    }

    @Override
    public UserModel userProfileUpdate(UserProfileRequestModel profileRequestModel) {
        UserEntity user = this.getUserContext();
        user.setName(profileRequestModel.getName());
        user.setLocation(profileRequestModel.getLocation());
        List<GenreEntity> genres = new ArrayList<>();
        profileRequestModel.getInterests().forEach(interest -> {
            if(genreRepository.findByGenreName(interest).isEmpty()){
                GenreEntity genre = new GenreEntity();
                genre.setGenreName(interest);
                genres.add(genre);
            } else {
                genres.add(genreRepository.findByGenreName(interest).get());
            }
        });
        genreRepository.saveAll(genres);
        user.setInterests(genres);
        userRepository.save(user);
        return userMapper.entityToModel(user);
    }

    @Override
    public void sendVerificationEmail(String email) {
        String token = tokenUtils
                .generateActionTokenForUser(email, ActionType.EMAIL_VERIFICATION,
                        Instant.now().plus(1, ChronoUnit.DAYS));
        EmailModel emailModel = new EmailModel();
        emailModel.setTo(email);
        emailModel.setSubject("Verify you Bioscope account");
        emailModel.setTemplate("verify-account");
        final String verificationLink =
               api_url + "/v01/auth/verify-account?token=" + token + "&email=" + email;

        emailModel.setVariables(Map.of(
            "email", email,
                "verificationUrl", verificationLink
        ));

        try {
            emailSender.sendEmail(emailModel);
            log.info("verification email sent to {}, link: {}", email, verificationLink);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyAccount(String token, String email) {

        Optional<ActionTokenEntity> optionalActionToken =
                tokenUtils.validateAndFindActionToken(token);
        if (optionalActionToken.isEmpty()) {
            throw new InvalidCredentialsException("Invalid token found");
        }

        ActionTokenEntity tokenEntity = optionalActionToken.get();
        if (!Objects.equals(email, tokenEntity.getEmail())) {
            log.warn("Using someone else's token user: {}", email);
            throw new InvalidCredentialsException("Using invalid token");
        }
            if (!tokenEntity.getActionType().equals(ActionType.EMAIL_VERIFICATION)) {
                log.warn("User tried verify with wrong token user email {}", email);
                throw new InvalidCredentialsException("Wrong token found");
            }
            Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                user.setEnabled(true);
                userRepository.save(user);
                tokenEntity.setUsed(true);
                actionTokenRepo.save(tokenEntity);
                return true;
            }
        throw new ResourceNotFoundException("User", "Email", email);

    }

    @Override
    public LoginResponse loginUser(UserRequestModel loginRequest) {
        final String email = loginRequest.getEmail();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );
        if (!user.getRole().name().equals(loginRequest.getRole().toUpperCase())) {
            throw new InvalidCredentialsException("Role", loginRequest.getRole());
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password", "Invalid");
        }
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Account", "Not verified");
        }
        String token = jwtProvider.generateAccessToken(user);
        return LoginResponse.builder()
                .token(token)
                .refreshToken(generateRefreshTokenForUser(user))
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken)  {

        log.info("Token received: {}", refreshToken);
        Optional<RefreshTokenEntity> optionalRefreshToken =
                tokenUtils.validateAndFindRefresh(refreshToken);
        if(optionalRefreshToken.isEmpty()) {
            log.error("Token entity not found with: {}", refreshToken);
            throw new TokenCycleFailedException("Refresh token not found");
        }

        RefreshTokenEntity tokenEntity = optionalRefreshToken.get();
        if (tokenEntity.isRevoked() || tokenEntity.getExpiresAt().isBefore(Instant.now())) {
            log.error("Token found but expired: {} and revoked: {} have id {}",
                    tokenEntity.getExpiresAt().isBefore(Instant.now()),
                    tokenEntity.isRevoked(), tokenEntity.getId());
            throw new TokenCycleFailedException("Revoked or expired token");
        }

        UserEntity user = tokenEntity.getUser();
        tokenEntity.setRevoked(true);
        refreshTokenRepo.save(tokenEntity);
        String token = jwtProvider.generateAccessToken(user);

        return LoginResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .refreshToken(generateRefreshTokenForUser(user))
                .build();
    }

    private String generateRefreshTokenForUser(UserEntity user) {
        byte [] tokenByte = tokenUtils.generateOpaqueBytes();
        RefreshTokenEntity refreshEntity =
                new RefreshTokenEntity();
        refreshEntity.setUser(user);
        refreshEntity.setClientType(ClientType.WEB);
        refreshEntity.setCreatedAt(Instant.now());
        refreshEntity.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshEntity.setRevoked(false);
        refreshEntity.setTokenHash(Hex.
                encodeHexString(tokenUtils.hashRefreshToken(tokenByte)));

        refreshTokenRepo.save(refreshEntity);
        userRepository.save(user);
        return
                Base64.getUrlEncoder().encodeToString(tokenByte);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

        UserEntity user = this.getUserContext();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Password", "Invalid");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String password) {
        UserEntity user = this.getUserContext();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Password", "Invalid");
        }
        userRepository.delete(user);
    }

    @Override
    public void logoutUser(String refreshToken) {
        Optional<RefreshTokenEntity> optionalRefreshToken =
                tokenUtils.validateAndFindRefresh(refreshToken);

        if(optionalRefreshToken.isEmpty()) {
            throw new InvalidCredentialsException("Invalid token: unable to logout");
        }

        RefreshTokenEntity entity =
                optionalRefreshToken.get();
        entity.setRevoked(true);
        refreshTokenRepo.save(entity);
        log.info("Logging out the user");
    }

    @Override
    public void forgotPasswordEmail(String email) {

        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email)
        );

        String token = tokenUtils.generateActionTokenForUser(email,
                ActionType.PASSWORD_RESET, Instant.now().plus(15, ChronoUnit.MINUTES));
        EmailModel emailModel = new EmailModel();
        emailModel.setTo(email);
        emailModel.setSubject("Reset your Bioscope password");
        emailModel.setTemplate("reset-password");
        final String verificationLink =
               ui_url + "/password-change/reset?email=" + email + "&token=" + token;

        var name = user.getName() == null ? "User" : user.getName();
        emailModel.setVariables(Map.of(
                "userName", name,
                "resetPasswordUrl", verificationLink
        ));

        try {
            emailSender.sendEmail(emailModel);
            log.info("Password Reset email sent to {}, link: {}", email, verificationLink);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", email, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean resetPassword(String token, String email, String newPassword) {

        if (token == null || email == null || newPassword == null) {
            throw new IllegalArgumentException("Please send valid information");
        }

        Optional<ActionTokenEntity> optionalActionToken =
                tokenUtils.validateAndFindActionToken(token);
        Optional<UserEntity> optionalUser =
                userRepository.findByEmail(email);

        if (optionalActionToken.isPresent() && optionalUser.isPresent()) {
            ActionTokenEntity tokenEntity =
                    optionalActionToken.get();
            UserEntity user = optionalUser.get();
            if (!tokenEntity.getEmail().equals(user.getEmail())) {
                throw new InvalidCredentialsException("Invalid token or email");
            }
            if (!tokenEntity.getActionType().equals(ActionType.PASSWORD_RESET)) {
                throw new InvalidCredentialsException("Not the right token");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            tokenEntity.setUsed(true);
            userRepository.save(user);
            actionTokenRepo.save(tokenEntity);
            return true;
        }

        throw new IllegalArgumentException("Failed to update password, please try again");
    }

    private  UserEntity getUserContext() {
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
