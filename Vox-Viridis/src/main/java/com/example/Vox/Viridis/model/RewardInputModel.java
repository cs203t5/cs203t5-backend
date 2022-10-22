package com.example.Vox.Viridis.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.example.Vox.Viridis.service.RewardTypeService;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardInputModel {
    @NotBlank(message = "rewardType can't be empty")
    private String rewardType;

    @NotBlank(message = "rewardName can't be empty")
    private String rewardName;

    @Min(value = 1, message = "goal must be at least 1")
    @NotNull
    private Integer goal;

    public Reward convertToReward(RewardTypeService rewardTypeService) {
        Reward reward = new Reward();
        reward.setRewardName(this.getRewardName());
        reward.setGoal(this.getGoal());
        reward.setRewardType(rewardTypeService.getRewardTypeByName(this.getRewardType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward type '" + this.getRewardType() + "'")));

        return reward;
    }
}