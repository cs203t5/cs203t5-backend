package com.example.Vox.Viridis.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.model.RewardType;
import com.example.Vox.Viridis.service.RewardTypeService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("rewardType")
@RequiredArgsConstructor
public class RewardTypeController {
    private final RewardTypeService rewardTypeService;

    @GetMapping
    public List<RewardType> getRewardTypes() {
        return rewardTypeService.getRewardType();
    }
}
