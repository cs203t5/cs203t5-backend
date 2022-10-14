package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class InvalidJsonException extends ValidationException {
    private static final long serialVersionUID = 1L;
    public InvalidJsonException(String field, Throwable throwable) {
        super(HttpStatus.BAD_REQUEST, "Invalid JSON input at field " + field, throwable);
    }
}
