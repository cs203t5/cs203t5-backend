package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidJsonException extends ValidationException {
    private static final long serialVersionUID = 1L;
    public InvalidJsonException(String field, Throwable throwable) {
        super("Invalid JSON input at field " + field, throwable);
    }
}
