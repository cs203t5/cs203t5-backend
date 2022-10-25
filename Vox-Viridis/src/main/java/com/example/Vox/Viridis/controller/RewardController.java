package com.example.Vox.Viridis.controller;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardInputModel;
import com.example.Vox.Viridis.service.RewardService;
import com.example.Vox.Viridis.service.RewardTypeService;
import com.example.Vox.Viridis.service.UsersService;
import lombok.RequiredArgsConstructor;

@Validated
@RestController()
@RequestMapping("reward")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;
    private final RewardTypeService rewardTypeService;
    private final UsersService usersService;

    @GetMapping("myReward")
    public List<Reward> getRewardsByUserId() {
        return rewardService.getRewardsByUserId(usersService.getCurrentUser().getAccountId());
    }

    @GetMapping()
    public List<Reward> getRewards() {
        return rewardService.getRewards();
    }

    @GetMapping("byCampaign/{campaignId}")
    public Reward getRewards(@PathVariable Long campaignId) {
        return rewardService.getRewardByCampaignId(campaignId).orElseThrow(() -> new ResourceNotFoundException(
            "Reward with campaign id " + campaignId));
    }

    @GetMapping("{id}")
    public Reward getRewardById(@PathVariable Long id) {
        return rewardService.getReward(id).orElseThrow(() -> new ResourceNotFoundException(
            "Reward id " + id));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Reward createRewards(@RequestParam(value="campaignId", required=true) Long campaignId,
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
