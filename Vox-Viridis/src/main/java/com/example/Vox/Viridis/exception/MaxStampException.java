package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class MaxStampException extends ValidationException {
    private static final long serialVersionUID = 1L;

    public MaxStampException() {
        super(HttpStatus.BAD_REQUEST, "User already have max number of stamp");
    }

    public MaxStampException(Long rewardId, int maxStamp) {
        super(HttpStatus.BAD_REQUEST, "User already have max number of stamp, " + maxStamp + " for Reward id " + rewardId);
    }
}
