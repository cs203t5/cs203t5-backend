package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Meant for other exception to extend to this exception
 * Used in RestExceptionHandler.java
 */
public abstract class ValidationException extends RuntimeException {

    @Getter
    private HttpStatus httpStatus;

    public ValidationException() {
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public ValidationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ValidationException(HttpStatus httpStatus, Throwable ex) {
        super(ex);
        this.httpStatus = httpStatus;
    }

    public ValidationException(HttpStatus httpStatus, String message, Throwable ex) {
        super(message, ex);
        this.httpStatus = httpStatus;
    }

}
