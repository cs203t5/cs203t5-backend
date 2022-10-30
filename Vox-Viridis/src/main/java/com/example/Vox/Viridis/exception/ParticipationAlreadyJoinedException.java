package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class ParticipationAlreadyJoinedException extends ValidationException {
    private static final long serialVersionUID = 1L;

    public ParticipationAlreadyJoinedException() {
        super(HttpStatus.BAD_REQUEST, "You have already participated this campaign");
    }

    public ParticipationAlreadyJoinedException(Long rewardId) {
        super(HttpStatus.BAD_REQUEST, "You have already participated this reward id " + rewardId);
    }
}
