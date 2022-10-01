package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CampaignExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public CampaignExistsException(String title) {
        super("This title exists: " + title);
    }
}
