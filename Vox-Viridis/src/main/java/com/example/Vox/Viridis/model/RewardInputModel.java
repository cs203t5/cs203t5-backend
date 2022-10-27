package com.example.Vox.Viridis.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.example.Vox.Viridis.service.RewardTypeService;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardInputModel {
    @NotBlank(message = "rewardType can't be empty")
    private String rewardType;

    @NotBlank(message = "rewardName can't be empty")
    private String rewardName;

    @Min(value = 1, message = "goal must be at least 1")
    @NotNull(message = "goal cannot be null")
    private Integer goal;

    @NotNull(message = "tnc cannot be null")
    @Size(min = 5, message = "tnc must be at least 5 letters")
    private String tnc; // terms and conditions

    public Reward convertToReward(RewardTypeService rewardTypeService) {
        Reward reward = new Reward();
        reward.setRewardName(this.getRewardName());
        reward.setGoal(this.getGoal());
        reward.setTnc(this.getTnc());
        reward.setRewardType(rewardTypeService.getRewardTypeByName(this.getRewardType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward type '" + this.getRewardType() + "'")));

        return reward;
    }
}