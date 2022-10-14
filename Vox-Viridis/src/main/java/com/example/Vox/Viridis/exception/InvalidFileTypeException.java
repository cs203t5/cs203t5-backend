package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class InvalidFileTypeException extends ValidationException {
    private static final long serialVersionUID = 1L;
    public InvalidFileTypeException(String supportedFile) {
        super(HttpStatus.BAD_REQUEST, "Invalid File type. Only support " + supportedFile);
    }
}
