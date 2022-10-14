package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class NotOwnerException extends ValidationException {
    public NotOwnerException() {
        super(HttpStatus.FORBIDDEN, "Not Authorised. You are not the owner of this.");
    }
}
