package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ValidationException {
    private static final long serialVersionUID = 1L;
    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Resource not found");
    }
    public ResourceNotFoundException(String resourceMessage) {
        super(HttpStatus.NOT_FOUND, resourceMessage + " doesn't exists");
    }
}
