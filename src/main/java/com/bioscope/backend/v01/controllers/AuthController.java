package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.security.JwtProvider;
import com.bioscope.backend.v01.services.iface.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v01/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody @Valid UserRequestModel userRequestModel) {

        UserModel userModel = authService.registerUser(userRequestModel);
        return new ResponseEntity<>(userModel, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody @Valid UserRequestModel loginRequest) {
        LoginResponse loginResponse = authService.loginUser(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<UserModel> updateUser(@RequestBody @Valid UserProfileRequestModel model){
        UserModel userModel = authService.userProfileUpdate(model);
        return new ResponseEntity<>(userModel, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken){
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @RequestMapping("/verify-account")
    public RedirectView verifyAccount(@RequestParam String token, @RequestParam String email, HttpServletRequest request){
        RedirectView view = new RedirectView();
        if (authService.verifyAccount(token, email)) {
             view.setUrl("http://localhost:4200/join");

        } else {
            view.setUrl("http://localhost:4200/error");
        }
        return view;
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestParam String token, @RequestParam String refreshToken){
        authService.logoutUser(token, refreshToken);
        return new ResponseEntity<>("Logged out", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String password){
        authService.deleteUser(password);
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword){
        authService.changePassword(oldPassword, newPassword);
        return new ResponseEntity<>("Password changed", HttpStatus.OK);
    }
}
