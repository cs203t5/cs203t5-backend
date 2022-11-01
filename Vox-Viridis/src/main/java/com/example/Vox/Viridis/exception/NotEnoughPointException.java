package com.example.Vox.Viridis.exception;
import org.springframework.http.HttpStatus;

public class NotEnoughPointException extends ValidationException{
    public NotEnoughPointException() {
        super(HttpStatus.BAD_REQUEST, "Insufficient point to purchase product.");
    }
}
