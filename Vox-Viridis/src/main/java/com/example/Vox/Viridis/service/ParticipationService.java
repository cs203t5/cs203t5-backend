package com.example.Vox.Viridis.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.MaxStampException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.repository.ParticipationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participations;
    private final UsersService usersService;
    private final RewardService rewardService;

    public PaginationDTO<Participation> getMyParticipation(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 20);
        Page<Participation> result = participations.findByUser(usersService.getCurrentUser(), pageable);
        return new PaginationDTO<>(result);
    }

    public int getMyPoints() {
        return usersService.getCurrentUser().getPoints();
    }

    /**
     * Create participation for this user, if Participation.reward doesn't exist for current user
     * @param rewardId
     * @throws ResourceNotFoundException if Reward with id of rewardId doesn't exist
     * @return created participation if successful. Else, null if there is already participation with reward same as rewardId, and belong to current user
     */
    public Participation createParticipation(Long rewardId) {
        Users currentUser = usersService.getCurrentUser();
        Reward reward = rewardService.getReward(rewardId)
            .orElseThrow(() -> new ResourceNotFoundException("Reward id " + rewardId));
        if (participations.findByRewardAndUser(reward, currentUser).isPresent()) {
            log.error("Participation record already exists for Reward id " + rewardId + " for user id " + currentUser.getAccountId());
            return null;
        }

        Participation participation = new Participation();
        participation.setParticipatedOn(LocalDateTime.now());
        participation.setUser(currentUser);
        participation.setReward(reward);

        log.info("Created Participation with rewardId " + rewardId + " and userId " + currentUser.getAccountId());
        return participations.save(participation);
    }

    /**
     * If RewardType = stamp, Add stamp to the Participation object
     * If RewardType = point, add {reward.goal} number of points to the Users object
     * @param noOfStamp noOfStamp to add to current Participation
     * @param id Participation id
     * @throws ResourceNotFoundException if participation id doesn't exist in Database
     * @return updated Participation
     */
    public Participation addNoOfStamps(long id, int noOfStamp) {
        Participation participation = participations.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Participation id " + id));
        
        if (!participation.getReward().getRewardType().getRewardType().equals("Cards")) {
            // If RewardType = Points, should update users.points instead
            // Will add {reward.goal} number of points
            Users customer = participation.getUser();
            customer.setPoints(customer.getPoints() + participation.getReward().getGoal());
            usersService.updateUser(customer);
            return participation;
        }
        
        final int maxStamp = participation.getReward().getGoal();
        if (participation.getNoOfStamp() >= maxStamp)
            throw new MaxStampException(participation.getReward().getId(), maxStamp);
        
        if (participation.getNoOfStamp() + noOfStamp >= maxStamp)
            participation.setNoOfStamp(maxStamp); // Can only get up to maxStamp
        else
            participation.setNoOfStamp(participation.getNoOfStamp() + noOfStamp);
        
        return participations.save(participation);
    }
}
