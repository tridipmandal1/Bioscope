package com.bioscope.backend.v01.services.iface;

import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;

public interface UserService {

    UserModel registerUser(UserRequestModel userRequestModel);
    UserModel findUserByEmail(String email);
    UserModel updateUserProfile(String userId, UserProfileRequestModel userProfileRequestModel);
}
