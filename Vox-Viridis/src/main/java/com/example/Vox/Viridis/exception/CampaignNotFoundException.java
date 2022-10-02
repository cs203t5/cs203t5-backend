package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CampaignNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public CampaignNotFoundException() {
        super("Campaign not found");
    }
    public CampaignNotFoundException(Long id) {
        super("Campaign id " + id + " not doesn't exist");
    }
}
