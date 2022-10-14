package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class CampaignTitleExistsException extends ValidationException {
    private static final long serialVersionUID = 1L;
    public CampaignTitleExistsException(String title) {
        super(HttpStatus.CONFLICT, "This title already exists: " + title);
    }
}
