package com.bioscope.backend.v01.exceptions;

import lombok.*;

@Getter
@Setter
public class InvalidCredentialsException extends RuntimeException {

    private String message;

    public InvalidCredentialsException(String fieldName, String fieldValue) {
        this.message =
                String.format("Invalid credentials provided: %s", fieldName + "=" + fieldValue);
    }

    public InvalidCredentialsException(String message) {
        this.message = message;
    }
}
