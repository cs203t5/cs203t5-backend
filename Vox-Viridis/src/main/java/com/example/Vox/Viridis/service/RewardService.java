package com.example.Vox.Viridis.service;

import java.util.ArrayList;
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
     * Get reward by offeredBy (campaignId)
     * 
     * @param campaignId
     */
    public List<Reward> getRewards(long campaignId) {
        return rewards.findByOfferedBy(campaignId);
    }

    public List<Reward> getRewardsByUserId(Long userid) {
        return rewards.findByUserId(userid);
    }

    /**
     * Get reward by id and offeredBy (campaignId)
     */
    public Optional<Reward> getReward(long rewardId, long campaignId) {
        return rewards.findByIdAndOfferedBy(rewardId, campaignId);
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     * @throws ResourceNotFoundException if campaign isn't found
     */
    public List<Reward> addReward(List<Reward> rewardsArr, long offeredByCampaignId) {
        Campaign offeredBy = campaignService.getCampaign(offeredByCampaignId).orElseThrow(
                () -> new ResourceNotFoundException("Campaign id " + offeredByCampaignId));

        return addReward(rewardsArr, offeredBy);
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     */
    public List<Reward> addReward(List<Reward> rewardsArr, Campaign offeredBy) {
        // validate that the campaign object belongs to the current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !offeredBy.getCreatedBy().equals(user))
            throw new NotOwnerException();

        rewardsArr.forEach(reward -> {
            reward.setOfferedBy(offeredBy);
        });

        log.info("Created " + rewardsArr.size() + " rewards for campaign id " + offeredBy.getId());
        return rewards.saveAll(rewardsArr);
    }

    public Reward addUserToReward(long rewardId, long campaignId) {
        Reward reward = rewards.findByIdAndOfferedBy(rewardId, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + rewardId + " with campaign id " + campaignId));

        Users user = usersService.getCurrentUser();
        reward.getUsers().add(user);
        Reward result = rewards.save(reward);
        log.info("username '" + user.getUsername() + "' added to reward id " + rewardId
                + " (campaign id " + campaignId + ")");
        return result;
    }

    /**
     * Update reward by id
     * 
     * @throws NotOwnerException if current user isn't the owner of this campaign's reward
     * @throws ResourceNotFoundException if reward isn't found
     * @return updated reward
     */
    public Reward updateReward(long id, long campaignId, Reward updatedReward) {
        Reward reward = rewards.findByIdAndOfferedBy(id, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + id + " with campaign id " + campaignId));
        updatedReward.setId(id);
        updatedReward.setOfferedBy(reward.getOfferedBy());
        updatedReward.setUsers(reward.getUsers());

        // validate it belongs to current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !updatedReward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        log.info("Updated record id " + id + " with campaignId " + campaignId);
        return rewards.save(updatedReward);
    }

    /**
     * Delete reward by id
     * 
     * @throws NotOwnerException if current user isn't the owner of this campaign's reward
     * @throws ResourceNotFoundException if reward isn't found
     */
    public void deleteReward(long id, long campaignId) {
        Reward reward = rewards.findByIdAndOfferedBy(id, campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reward id " + id + " with campaign id " + campaignId));

        // validate it belongs to current logged in user
        Users user = usersService.getCurrentUser();
        if (user != null && !reward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        log.info("Deleted record id " + id + " with campaignId " + campaignId + "by user id "
                + (user != null ? user.getAccount_id() : "null"));
        rewards.delete(reward);
    }
}
