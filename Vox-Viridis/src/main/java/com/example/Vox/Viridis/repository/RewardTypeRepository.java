package com.example.Vox.Viridis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Vox.Viridis.model.RewardType;

public interface RewardTypeRepository extends JpaRepository<RewardType, Long> {
    Optional<RewardType> findByRewardType(String rewardType);
}
