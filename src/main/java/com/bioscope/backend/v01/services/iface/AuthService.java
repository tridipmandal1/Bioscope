package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.entities.UserEntity;
import com.bioscope.backend.v01.exceptions.TokenCycleFailedException;
import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;

public interface AuthService {

    UserModel registerUser(UserRequestModel requestModel);
    UserModel userProfileUpdate(UserProfileRequestModel profileRequestModel);
    void sendVerificationEmail(String email);
    boolean verifyAccount(String token, String email);
    LoginResponse loginUser(UserRequestModel loginRequest);
    LoginResponse refreshToken(String refreshToken);
    void changePassword(String oldPassword, String newPassword);
    void deleteUser(String password);
    void logoutUser(String refreshToken);
    void forgotPasswordEmail(String email);
    boolean resetPassword(String token, String email, String newPassword);
}
