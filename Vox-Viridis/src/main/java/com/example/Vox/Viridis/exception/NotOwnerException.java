package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotOwnerException extends RuntimeException {
    public NotOwnerException() {
        super("Not Authorised. You are not the owner of this.");
    }
}
