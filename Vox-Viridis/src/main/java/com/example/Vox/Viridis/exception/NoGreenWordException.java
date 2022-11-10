package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class NoGreenWordException extends ValidationException {
    private static final long serialVersionUID = 1L;

    public NoGreenWordException() {
        super(HttpStatus.BAD_REQUEST, "Campaign title does not contain any of the whitelisted words");
    }

    public NoGreenWordException(Long campaignId) {
        super(HttpStatus.BAD_REQUEST, "Campaign id " + campaignId + " title does not contain any of the whitelisted words");
    }
}
