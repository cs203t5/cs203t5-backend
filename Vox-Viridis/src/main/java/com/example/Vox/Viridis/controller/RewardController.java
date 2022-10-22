package com.example.Vox.Viridis.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.service.RewardService;
import com.example.Vox.Viridis.service.RewardTypeService;
import com.example.Vox.Viridis.service.UsersService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Validated
@RestController()
@RequestMapping("campaign/{campaignId}/reward")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;
    private final RewardTypeService rewardTypeService;
    private final UsersService usersService;

    @GetMapping("/{userid}")
    public List<Reward> getRewardsByUserId(Authentication authentication) {
        return rewardService.getRewardsByUserId(usersService.getCurrentUser().getAccountId());
    }

    @GetMapping()
    public Reward getRewards(@PathVariable Long campaignId) {
        return rewardService.getRewardByCampaignId(campaignId).orElseThrow(() -> new ResourceNotFoundException(
            "Reward with campaign id " + campaignId));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Reward createRewards(@PathVariable Long campaignId,
            @Valid @RequestBody RewardInputModel input) {
        Reward reward = input.convertToReward(rewardTypeService);

        return rewardService.addReward(reward, campaignId);
    }

    @PostMapping("{id}/join")
    public void joinReward(@PathVariable Long id) {
        rewardService.addUserToReward(id);
    }

    @PutMapping("{id}")
    public Reward updateReward(@PathVariable Long id,
            @RequestBody @Valid RewardInputModel input) {
        return rewardService.updateReward(id, input.convertToReward(rewardTypeService));
    }

    @DeleteMapping("{id}")
    public void deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
    }
}


@Getter
@Setter
class RewardInputModel {
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
