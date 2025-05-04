package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.exceptions.InvalidCredentialsException;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.UserMapper;
import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.UserRepository;
import com.bioscope.backend.v01.security.JwtProvider;
import com.bioscope.backend.v01.services.iface.AuthService;
import com.bioscope.backend.v01.services.iface.TokenBlacklistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final GenreRepository genreRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            TokenBlacklistService tokenBlacklistService,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            GenreRepository genreRepository
    ) {
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.genreRepository = genreRepository;
    }


    @Override
    public UserModel registerUser(UserRequestModel requestModel) {
        if (userRepository.findByEmail(requestModel.getEmail()).isPresent()) {
            throw new AlreadyExistsException("User", "email", requestModel.getEmail());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(requestModel.getEmail());
        userEntity.setPassword(passwordEncoder.encode(requestModel.getPassword()));
        userEntity.setRole(Roles.valueOf(requestModel.getRole().toUpperCase()));
        userEntity.setEnabled(true); // TODO: Implement email verification
        userRepository.save(userEntity);
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
        user.setInterests(genres);
        userRepository.save(user);
        return userMapper.entityToModel(user);
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
        String token = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        UserEntity user =
                userRepository.findByEmail(jwtProvider
                        .extractUsernameFromToken(refreshToken)).orElseThrow(
                        () -> new ResourceNotFoundException("User", "email", refreshToken)
                );

        String token = jwtProvider.generateAccessToken(user);
        String refresh = jwtProvider.generateRefreshToken(user);

        return LoginResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .refreshToken(refresh)
                .build();
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
    public void logoutUser(String token, String refreshToken) {
        tokenBlacklistService.addTokenToBlacklist(token);
        tokenBlacklistService.addTokenToBlacklist(refreshToken);
    }

    private  UserEntity getUserContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User is not authenticated");
        }
        String username;
        Object principal = authentication.getPrincipal();
        System.out.println("Principal: " + principal);
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Failed to fetch user: " + username));

    }

}
