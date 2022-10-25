package com.example.Vox.Viridis.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.ParticipationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participations;
    private final UsersService usersService;
    private final EntityManager entityManager;

    /**
     * Create participation for this user, if Participation.reward doesn't exist for current user
     * @param rewardId
     * @throws ResourceNotFoundException if Reward with id of rewardId doesn't exist
     * @return created participation if successful. Else, null if there is already participation with reward same as rewardId, and belong to current user
     */
    public Participation createParticipation(Long rewardId) {
        Users currentUser = usersService.getCurrentUser();
        Reward reward = entityManager.getReference(Reward.class, rewardId);
        if (participations.findByRewardAndUser(reward, currentUser).isPresent()) {
            log.error("Reward id " + rewardId + " doesn't exist");
            return null;
        }

        Participation participation = new Participation();
        participation.setUser(currentUser);
        try {
            participation.setReward(reward);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Reward id " + rewardId, e);
        }

        log.info("Created Participation with rewardId " + rewardId + " and userId " + currentUser.getAccountId());
        return participations.save(participation);
    }

    /**
     * Add stamp to the Participation object
     * @param noOfStamp noOfStamp to add to current Participation
     * @param id Participation id
     * @throws ResourceNotFoundException if participation id doesn't exist in Database
     * @return updated Participation
     */
    public Participation addNoOfStamps(long id, int noOfStamp) {
        Participation participation = participations.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Participation id " + id));
        participation.setNoOfStamp(participation.getNoOfStamp() + noOfStamp);
        return participations.save(participation);
    }
}
