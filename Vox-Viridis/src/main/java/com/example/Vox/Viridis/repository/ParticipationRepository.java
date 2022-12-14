package com.example.Vox.Viridis.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<Participation> findByRewardAndUser(Reward reward, Users user);
    Page<Participation> findByUser(Users user, Pageable pageable);
}
