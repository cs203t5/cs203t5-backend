package com.example.Vox.Viridis.exception;

import org.springframework.http.HttpStatus;

public class CampaignAlreadyHasReward extends ValidationException {
    private static final long serialVersionUID = 1L;

    public CampaignAlreadyHasReward() {
        super(HttpStatus.BAD_REQUEST, "Campaign already has Reward");
    }

    public CampaignAlreadyHasReward(Long campaignId, Long rewardId) {
        super(HttpStatus.BAD_REQUEST, "Campaign id " + campaignId + " already has Reward id " + rewardId);
    }
}
