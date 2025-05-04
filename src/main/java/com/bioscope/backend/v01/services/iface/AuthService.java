package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;

public interface AuthService {

    UserModel registerUser(UserRequestModel requestModel);
    UserModel userProfileUpdate(UserProfileRequestModel profileRequestModel);
    LoginResponse loginUser(UserRequestModel loginRequest);
    LoginResponse refreshToken(String refreshToken);
    void changePassword(String oldPassword, String newPassword);
    void deleteUser(String password);
    void logoutUser(String token, String refreshToken);
}
