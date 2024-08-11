package com.bioscope.backend.v01.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceNotFoundException extends RuntimeException {

   private String message;

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        this.message =
                String.format("Resource not found with %s with %s: %s", resourceName, fieldName, fieldValue);
    }
}
