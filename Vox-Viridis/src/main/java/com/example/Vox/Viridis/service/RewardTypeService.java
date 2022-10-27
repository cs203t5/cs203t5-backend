package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.model.RewardType;
import com.example.Vox.Viridis.repository.RewardTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardTypeService {
    private final RewardTypeRepository rewardTypes;

    public Optional<RewardType> getRewardTypeByName(String rewardType) {
        return rewardTypes.findByRewardType(rewardType);
    }

    public List<RewardType> getRewardType() {
        return rewardTypes.findAll();
    }
}
