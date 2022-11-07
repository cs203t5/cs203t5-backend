package com.example.Vox.Viridis.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
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
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.service.RewardService;
import com.example.Vox.Viridis.service.RewardTypeService;
import lombok.RequiredArgsConstructor;

@Validated
@RestController()
@RequestMapping("reward")
@RequiredArgsConstructor
public class RewardController {
    private final RewardService rewardService;
    private final RewardTypeService rewardTypeService;

    @GetMapping("myReward")
    public PaginationDTO<Reward> getRewardsByUserId(@RequestParam(value = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null)
            pageNum = 0;
        return rewardService.getRewardsByCurrentUser(pageNum);
    }

    @GetMapping()
    public PaginationDTO<Reward> getRewards(@RequestParam(value = "pageNum", required = false) Integer pageNum) {
        if (pageNum == null)
            pageNum = 0;
        return rewardService.getRewards(pageNum);
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

    @PostMapping("{campaignId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Reward createRewards(@PathVariable Long campaignId,
            @Valid @RequestBody RewardInputModel input) {
        Reward reward = input.convertToReward(rewardTypeService);

        return rewardService.addReward(reward, campaignId);
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
