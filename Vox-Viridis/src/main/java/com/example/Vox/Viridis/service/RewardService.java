package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.NotOwnerException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.RewardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardService {
    private final UsersService usersService;
    private final RewardRepository rewards;
    private final CampaignService campaignService;

    /**
     * Will get rewards that are not expired (or hasn't ended)
     * @return List of rewards that hasn't ended
     */
    public List<Reward> getRewards() {
        return rewards.findAllNotEnded();
    }

    /**
     * Get reward by offeredBy (campaignId)
     * 
     * @param campaignId
     */
    public Optional<Reward> getRewardByCampaignId(long campaignId) {
        return rewards.findByOfferedBy(campaignId);
    }

    public List<Reward> getRewardsByUserId(Long userid) {
        return rewards.findByUsers_accountId(userid);
    }

    /**
     * Get reward by id
     */
    public Optional<Reward> getReward(long rewardId) {
        return rewards.findById(rewardId);
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     * @throws ResourceNotFoundException if campaign isn't found
     */
    public Reward addReward(Reward rewardsArr, long offeredByCampaignId) {
        Campaign offeredBy = campaignService.getCampaign(offeredByCampaignId).orElseThrow(
                () -> new ResourceNotFoundException("Campaign id " + offeredByCampaignId));

        return addReward(rewardsArr, offeredBy);
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     */
    public Reward addReward(Reward reward, Campaign offeredBy) {
        // validate that the campaign object belongs to the current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !offeredBy.getCreatedBy().equals(user))
            throw new NotOwnerException();

        reward.setOfferedBy(offeredBy);

        log.info("Created reward id " + reward.getId() + " for campaign id " + offeredBy.getId());
        return rewards.save(reward);
    }

    public Reward addUserToReward(long rewardId) {
        Reward reward = rewards.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + rewardId));

        Users user = usersService.getCurrentUser();
        reward.getUsers().add(user);
        Reward result = rewards.save(reward);
        log.info("username '" + user.getUsername() + "' added to reward id " + rewardId);
        return result;
    }

    public Reward addUserToRewardByCampaignId(long campaignId) {
        Reward reward = rewards.findByOfferedBy(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward with Campaign id " + campaignId));

        Users user = usersService.getCurrentUser();
        reward.getUsers().add(user);
        Reward result = rewards.save(reward);
        log.info("username '" + user.getUsername() + "' added to reward id " + reward.getId() + " (campaign id" + campaignId + ")");
        return result;
    }

    /**
     * Update reward by id
     * 
     * @throws NotOwnerException if current user isn't the owner of this campaign's reward
     * @throws ResourceNotFoundException if reward isn't found
     * @return updated reward
     */
    public Reward updateReward(long id, Reward updatedReward) {
        Reward reward = rewards.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + id));
        updatedReward.setId(id);
        updatedReward.setOfferedBy(reward.getOfferedBy());
        updatedReward.setUsers(reward.getUsers());

        // validate it belongs to current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !updatedReward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        log.info("Updated record id " + id);
        return rewards.save(updatedReward);
    }

    /**
     * Delete reward by id
     * 
     * @throws NotOwnerException if current user isn't the owner of this campaign's reward
     * @throws ResourceNotFoundException if reward isn't found
     */
    public void deleteReward(long id) {
        Reward reward = rewards.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + id));

        // validate it belongs to current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !reward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        log.info("Deleted record id " + id + " with campaignId " + reward.getOfferedBy().getId() + "by user id "
                + (user != null ? user.getAccountId() : "null"));
        rewards.delete(reward);
    }
}
