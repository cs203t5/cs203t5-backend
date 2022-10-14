package com.example.Vox.Viridis.exception;

/**
 * Meant for other exception to extend to this exception
 * Used in RestExceptionHandler.java
 */
public abstract class ValidationException extends RuntimeException {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable ex) {
        super(ex);
    }

    public ValidationException(String message, Throwable ex) {
        super(message, ex);
    }

}
