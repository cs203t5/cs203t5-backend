package com.example.Vox.Viridis.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Vox.Viridis.model.Reward;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    @Query(value = "SELECT r FROM Reward r WHERE (SELECT COUNT(c) FROM r.offeredBy c) > 0")
    Page<Reward> findAllNotEnded(Pageable pageable);

    @Query(value = "SELECT r FROM Reward r WHERE offered_by=:campaignId")
    Optional<Reward> findByOfferedBy(long campaignId);

    @Query(value = "SELECT r FROM Reward r WHERE (SELECT COUNT(p) FROM r.participations p WHERE p.user.accountId = :user) > 0")
    Page<Reward> findByUsers_accountId(long user, Pageable pageable);
}
