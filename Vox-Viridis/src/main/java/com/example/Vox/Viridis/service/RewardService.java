package com.example.Vox.Viridis.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.CampaignAlreadyHasReward;
import com.example.Vox.Viridis.exception.NotOwnerException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
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
    private final StorageService storageService;

    /**
     * Will get rewards that are not expired (or hasn't ended)
     * @return List of rewards that hasn't ended
     */
    public PaginationDTO<Reward> getRewards(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 20);
        
        Page<Reward> result = rewards.findAllNotEnded(pageable);
        result.getContent().forEach(reward -> reward.constructCampaignImage(storageService));
        return new PaginationDTO<>(result);
    }

    /**
     * Get reward by offeredBy (campaignId)
     * 
     * @param campaignId
     */
    public Optional<Reward> getRewardByCampaignId(long campaignId) {
        Optional<Reward> result = rewards.findByOfferedBy(campaignId);
        result.ifPresent(reward -> reward.constructCampaignImage(storageService));
        return result;
    }

    public PaginationDTO<Reward> getRewardsByCurrentUser(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 20);

        Page<Reward> result = rewards.findByUsers_accountId(usersService.getCurrentUser().getAccountId(), pageable);
        result.forEach(reward -> reward.constructCampaignImage(storageService));
        return new PaginationDTO<>(result);
    }

    /**
     * Get reward by id
     */
    public Optional<Reward> getReward(long rewardId) {
        Optional<Reward> result = rewards.findById(rewardId);
        result.ifPresent(reward -> reward.constructCampaignImage(storageService));
        return result;
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     * @throws ResourceNotFoundException if campaign isn't found
     */
    public Reward addReward(Reward reward, long offeredByCampaignId) {
        Campaign offeredBy = campaignService.getCampaign(offeredByCampaignId).orElseThrow(
                () -> new ResourceNotFoundException("Campaign id " + offeredByCampaignId));

        return addReward(reward, offeredBy);
    }

    /**
     * Add reward
     * 
     * @throws NotOwnerException if current user isn't the owner of the campaign
     */
    public Reward addReward(Reward reward, Campaign offeredBy) {
        // validate that the campaign object belongs to the current logged in user
        Users user = usersService.getCurrentUser();
        if (!offeredBy.getCreatedBy().equals(user))
            throw new NotOwnerException();

        // check if campaign alr has reward
        if (offeredBy.getRewards() != null) 
            throw new CampaignAlreadyHasReward(offeredBy.getId(), offeredBy.getRewards().getId());

        reward.setOfferedBy(offeredBy);

        Reward result = rewards.save(reward);
        result.constructCampaignImage(storageService);
        log.info("Created reward id " + reward.getId() + " for campaign id " + offeredBy.getId());
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
        updatedReward.setParticipations(reward.getParticipations());

        // validate it belongs to current logged in user
        Users user = usersService.getCurrentUser();
        if (!updatedReward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        updatedReward = rewards.save(updatedReward);
        log.info("Updated record id " + id);
        updatedReward.constructCampaignImage(storageService);
        return updatedReward;
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
        if (!reward.getOfferedBy().getCreatedBy().equals(user))
            throw new NotOwnerException();

        log.info("Deleted record id " + id + " with campaignId " + reward.getOfferedBy().getId() + "by user id "
                + user.getAccountId());
        rewards.delete(reward);
    }
}
