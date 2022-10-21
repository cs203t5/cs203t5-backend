package com.example.Vox.Viridis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Vox.Viridis.model.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    @Query(value = "SELECT r FROM Reward r WHERE offered_by=:campaignId")
    List<Reward> findByOfferedBy(long campaignId);

    @Query("SELECT r FROM Reward r WHERE id=:id AND offered_by=:campaignId")
    Optional<Reward> findByIdAndOfferedBy(long id, long campaignId);

    List<Reward> findByUsers_accountId(long userId);
}
