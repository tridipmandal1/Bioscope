package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.services.iface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v01/user")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody UserRequestModel userRequestModel){
        UserModel user = userService.registerUser(userRequestModel);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<UserModel> updateUserProfile(@PathVariable String userId, @RequestBody UserProfileRequestModel userProfileRequestModel){
        UserModel user = userService.updateUserProfile(userId, userProfileRequestModel);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
