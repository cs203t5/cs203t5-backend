package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public InvalidFileTypeException(String supportedFile) {
        super("Invalid File type. Only support " + supportedFile);
    }
}
