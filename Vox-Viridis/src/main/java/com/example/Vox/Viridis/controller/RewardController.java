package com.example.Vox.Viridis.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.service.RewardService;
import com.example.Vox.Viridis.service.RewardTypeService;

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

    @GetMapping("/{userid}")
    public List<Reward> getRewardsByUserId(Authentication authentication) {
        return rewardService
                .getRewardsByUserId(((Users) authentication.getPrincipal()).getAccountId());
    }

    @GetMapping()
    public List<Reward> getRewards(@PathVariable Long campaignId) {
        return rewardService.getRewards(campaignId);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public List<Reward> createRewards(@PathVariable Long campaignId,
            @Valid @RequestBody List<RewardInputModel> input) {
        List<Reward> rewards = input.stream()
                .map(rewardInput -> rewardInput.convertToReward(rewardTypeService)).toList();

        return rewardService.addReward(rewards, campaignId);
    }

    @PostMapping("{id}/join")
    public void joinReward(@PathVariable Long campaignId, @PathVariable Long id) {
        rewardService.addUserToReward(id, campaignId);
    }

    @PutMapping("{id}")
    public Reward updateReward(@PathVariable Long campaignId, @PathVariable Long id,
            @RequestBody @Valid RewardInputModel input) {
        return rewardService.updateReward(id, campaignId, input.convertToReward(rewardTypeService));
    }

    @DeleteMapping("{id}")
    public void deleteReward(@PathVariable Long campaignId, @PathVariable Long id) {
        rewardService.deleteReward(id, campaignId);
    }
}


@Getter
@Setter
class RewardInputModel {
    @NotBlank(message = "rewardType can't be empty")
    private String rewardType;

    @NotBlank(message = "rewardName can't be empty")
    private String rewardName;

    public Reward convertToReward(RewardTypeService rewardTypeService) {
        Reward reward = new Reward();
        reward.setRewardName(this.getRewardName());
        reward.setRewardType(rewardTypeService.getRewardTypeByName(this.getRewardType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward type '" + this.getRewardType() + "'")));

        return reward;
    }
}
