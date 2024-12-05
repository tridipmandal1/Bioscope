package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.entities.GenreEntity;
import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.mapper.UserMapper;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.repos.GenreRepository;
import com.bioscope.backend.v01.repos.UserRepository;
import com.bioscope.backend.v01.services.iface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GenreRepository genreRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.genreRepository = genreRepository;
    }

    @Override
    public UserModel registerUser(UserRequestModel userRequestModel) {
        String email = userRequestModel.getEmail();
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if(userEntity.isPresent()){
            throw new AlreadyExistsException("User with email: " + email + " already exists");
        }
        UserEntity userToCreate = new UserEntity();
        userToCreate.setEmail(email);
        /**
         * @TODO
         * Have to change it. password should be encrypted
         */
        userToCreate.setPassword(userRequestModel.getPassword());
        return userMapper.entityToModel(userRepository.save(userToCreate));
    }

    @Override
    public UserModel findUserByEmail(String email) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if(userEntity.isEmpty()){
            throw new ResourceNotFoundException("User with email: " + email + " not found");
        }
        return userMapper.entityToModel(userEntity.get());
    }

    /**
     * @param userId
     * @param userProfileRequestModel
     * @return {@link UserModel}
     */
    @Override
    public UserModel updateUserProfile(String userId, UserProfileRequestModel userProfileRequestModel) {
        Optional<UserEntity> userFound =  userRepository.findById(UUID.fromString(userId));
        if(userFound.isEmpty()){
            throw new ResourceNotFoundException("User with id: " + userId + " not found");
        }
        UserEntity userToUpdate = userFound.get();
        userToUpdate.setName(userProfileRequestModel.getName());
        userToUpdate.setCurrentLocation(userProfileRequestModel.getHometown());
        List<String> interests = userProfileRequestModel.getInterests();
        if(interests != null){
            List<GenreEntity> genreEntities = new ArrayList<>();
            interests.forEach(interest -> {
                Optional<GenreEntity> genreEntity = genreRepository.findByGenreName(interest);
                if(genreEntity.isEmpty()){
                    GenreEntity genreToCreate = new GenreEntity();
                    genreToCreate.setGenreName(interest);
                    genreEntities.add(genreRepository.save(genreToCreate));
                }else{
                    genreEntities.add(genreEntity.get());
                }
            });
            userToUpdate.setInterests(genreEntities);
        }
        return userMapper.entityToModel(userRepository.save(userToUpdate));
    }
}
