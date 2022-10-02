package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InconsistentDateException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InconsistentDateException() {
        super("Invalid dates: end date must be after start date");
    }
    public InconsistentDateException(String message) {
        super("Invalid dates: " + message);
    }
}
