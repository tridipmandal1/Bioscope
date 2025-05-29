package com.bioscope.backend.v01.handler;

import com.bioscope.backend.v01.exceptions.AlreadyExistsException;
import com.bioscope.backend.v01.exceptions.ResourceNotFoundException;
import com.bioscope.backend.v01.models.ApiResponse;
import com.razorpay.RazorpayException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ValidationException;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.DateTimeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException rfe){

        String message = rfe.getMessage();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(message)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse> alreadyExistsExceptionHandler(AlreadyExistsException existsException) {
        String message = existsException.getMessage();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(message)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse> validationExceptionHandler(ValidationException validationException) {
        String message = validationException.getMessage();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(message)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> illegalArgumentExceptionHandler(IllegalArgumentException illegalArgumentException) {
        String message = illegalArgumentException.getMessage();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(message)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> runtimeExceptionHandler(RuntimeException runtimeException) {
        String message = runtimeException.getMessage();
        ApiResponse apiResponse = ApiResponse.builder()
                .message(message)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleSecurityException(Exception exception) {
        final String message = exception.getMessage();
        if (exception instanceof BadCredentialsException) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .message("Invalid username or password")
                    .status(false)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
        if (exception instanceof AccountStatusException) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .message("Account is disabled")
                    .status(false)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

        if (exception instanceof DateTimeException) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .message("Invalid date or time format")
                    .status(false)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }

        if (exception instanceof AccessDeniedException) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .message("Access Denied")
                    .status(false)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
        }
        if (exception instanceof SignatureException) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .message("Invalid token")
                    .status(false)
                    .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
        ApiResponse apiResponse;
        if (exception instanceof IOException) {
            apiResponse = ApiResponse.builder()
                    .message("Error reading internal file")
                    .status(false)
                    .build();

        }
        if (exception instanceof RazorpayException) {
            apiResponse = ApiResponse.builder()
                    .message("Error in payment gateway")
                    .status(false)
                    .build();
        } else if (exception instanceof JSONException) {
            apiResponse = ApiResponse.builder()
                    .message("Error creating json object")
                    .status(false)
                    .build();
        }
        else {
            apiResponse = ApiResponse.builder()
                    .message(message)
                    .status(false)
                    .build();
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
