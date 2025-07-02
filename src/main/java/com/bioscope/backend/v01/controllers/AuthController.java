package com.bioscope.backend.v01.controllers;

import com.bioscope.backend.v01.enums.Roles;
import com.bioscope.backend.v01.models.ApiResponse;
import com.bioscope.backend.v01.models.LoginResponse;
import com.bioscope.backend.v01.models.user.UserModel;
import com.bioscope.backend.v01.models.user.UserProfileRequestModel;
import com.bioscope.backend.v01.models.user.UserRequestModel;
import com.bioscope.backend.v01.repos.UserRepository;
import com.bioscope.backend.v01.security.JwtProvider;
import com.bioscope.backend.v01.services.iface.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/v01/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${ui.uri}")
    private String ui_url;


    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
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
             view.setUrl(ui_url + "/join");

        } else {
            view.setUrl(ui_url + "/error");
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
    public ResponseEntity<ApiResponse> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword){
        authService.changePassword(oldPassword, newPassword);
        var res = ApiResponse.builder()
                .message("Password changed")
                .status(true)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPasswordEmail(@RequestParam String email) {
        authService.forgotPasswordEmail(email);
        var res = ApiResponse.builder()
                .message("Email sent")
                .status(true)
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword( @RequestParam String email,
                                       @RequestParam String token,
                                       @RequestParam String newPassword) {
        boolean isReset = authService.resetPassword(token, email, newPassword);
        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with email: " + email)
        );
        var resp = new ApiResponse();
        resp.setStatus(isReset);
        if (isReset) {
            if (user.getRole().equals(Roles.HOST)) {
                resp.setMessage("HOST");
            } else {
                resp.setMessage("USER");
            }
        } else {
            resp.setMessage("ERROR");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
