package com.example.Vox.Viridis.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.repository.RewardRepository;

@ExtendWith(MockitoExtension.class)
public class RewardServiceTest {
    @Mock
    private RewardRepository rewards;

    @Mock
    private UsersService usersService;

    @Mock
    private StorageService storageService;

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private RewardService rewardService;
    
    @Test
    void getRewards_Page1_ReturnRewards() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setRewardName("New Campaign");
        reward.setOfferedBy(new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", reward, createdBy, LocalDateTime.now(), null));

        Pageable pageable = PageRequest.of(0, 20);

        when(rewards.findAllNotEnded(pageable))
                .thenReturn(new PageImpl<>(List.of(reward)));
        when(storageService.getUrl(null)).thenReturn(null);

        PaginationDTO<Reward> result = rewardService.getRewards(0);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        assertEquals(result.getElements().size(), 1);
        assertEquals(result.getTotalNumPage(), 1);
        assertEquals(result.getElements().get(0), reward);
        verify(rewards).findAllNotEnded(pageable);
        verify(storageService).getUrl(null);
    }
    
    @Test
    void getRewardByCampaignId_ReturnReward() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setRewardName("New Campaign");
        reward.setOfferedBy(new Campaign(2l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", reward, createdBy, LocalDateTime.now(), null));

        when(rewards.findByOfferedBy(any(Long.class)))
                .thenReturn(Optional.of(reward));
        when(storageService.getUrl(null)).thenReturn(null);

        Optional<Reward> result = rewardService.getRewardByCampaignId(reward.getOfferedBy().getId());

        assertTrue(result.isPresent());
        assertEquals(result.get(), reward);
        verify(rewards).findByOfferedBy(reward.getOfferedBy().getId());
        verify(storageService).getUrl(null);
    }
    
    @Test
    void getRewardsByCurrentUser_Page1_ReturnRewards() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setRewardName("New Campaign");
        reward.setOfferedBy(new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", reward, createdBy, LocalDateTime.now(), null));

        Pageable pageable = PageRequest.of(0, 20);

        when(usersService.getCurrentUser()).thenReturn(createdBy);
        when(rewards.findByUsers_accountId(createdBy.getAccountId(), pageable))
                .thenReturn(new PageImpl<>(List.of(reward)));
        when(storageService.getUrl(null)).thenReturn(null);

        PaginationDTO<Reward> result = rewardService.getRewardsByCurrentUser(0);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), 1);
        assertEquals(result.getElements().size(), 1);
        assertEquals(result.getTotalNumPage(), 1);
        assertEquals(result.getElements().get(0), reward);
        verify(rewards).findByUsers_accountId(createdBy.getAccountId(), pageable);
        verify(usersService).getCurrentUser();
        verify(storageService).getUrl(null);
    }
    
    @Test
    void getRewardById_ReturnReward() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setId(1l);
        reward.setRewardName("New Campaign");
        reward.setOfferedBy(new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", reward, createdBy, LocalDateTime.now(), null));

        when(rewards.findById(any(Long.class)))
                .thenReturn(Optional.of(reward));
        when(storageService.getUrl(null)).thenReturn(null);

        Optional<Reward> result = rewardService.getReward(reward.getId());

        assertTrue(result.isPresent());
        assertEquals(result.get(), reward);
        verify(rewards).findById(reward.getId());
        verify(storageService).getUrl(null);
    }
    
    @Test
    void addReward_ByCampaignId_ReturnReward() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setId(1l);
        reward.setRewardName("New Campaign");
        Campaign offeredBy = new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", null, createdBy, LocalDateTime.now(), null);

        when(campaignService.getCampaign(any(Long.class)))
                .thenReturn(Optional.of(offeredBy));
        when(storageService.getUrl(null)).thenReturn(null);
        when(usersService.getCurrentUser()).thenReturn(createdBy);
        when(rewards.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reward result = rewardService.addReward(reward, offeredBy.getId());

        reward.setOfferedBy(offeredBy);
        assertEquals(result, reward);
        verify(campaignService).getCampaign(offeredBy.getId());
        verify(rewards).save(reward);
        verify(storageService).getUrl(null);
        verify(usersService).getCurrentUser();
    }
    
    @Test
    void addReward_ByCampaignObject_ReturnReward() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setId(1l);
        reward.setRewardName("New Campaign");
        Campaign offeredBy = new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", null, createdBy, LocalDateTime.now(), null);

        when(storageService.getUrl(null)).thenReturn(null);
        when(usersService.getCurrentUser()).thenReturn(createdBy);
        when(rewards.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reward result = rewardService.addReward(reward, offeredBy);

        reward.setOfferedBy(offeredBy);
        assertEquals(result, reward);
        verify(rewards).save(reward);
        verify(storageService).getUrl(null);
        verify(usersService).getCurrentUser();
    }
    
    @Test
    void updateReward_ReturnUpdatedReward() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setId(1l);
        reward.setRewardName("New Campaign");
        Campaign offeredBy = new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", null, createdBy, LocalDateTime.now(), null);
        reward.setOfferedBy(offeredBy);

        Reward updatedReward = new Reward();
        updatedReward.setRewardName("New Campaign");

        when(rewards.findById(any(Long.class))).thenReturn(Optional.of(reward));
        when(storageService.getUrl(null)).thenReturn(null);
        when(usersService.getCurrentUser()).thenReturn(createdBy);
        when(rewards.save(any(Reward.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reward result = rewardService.updateReward(reward.getId(), updatedReward);

        reward.setRewardName(updatedReward.getRewardName());
        assertEquals(result, reward);
        verify(rewards).findById(reward.getId());
        verify(rewards).save(reward);
        verify(storageService).getUrl(null);
        verify(usersService).getCurrentUser();
    }
    
    @Test
    void deleteReward_Success() {
        Users createdBy = new Users();
        createdBy.setAccountId(1l);
        createdBy.setEmail("test@test.com");
        createdBy.setFirstName("campaign");
        createdBy.setLastName("Admin");
        createdBy.setUsername("admin");
        Reward reward = new Reward();
        reward.setId(1l);
        reward.setRewardName("New Campaign");
        Campaign offeredBy = new Campaign(1l, "campaign title", "campaign desc", LocalDateTime.now(), LocalDateTime.now().plusDays(1), "North", "SMU Address", null, "plastic", null, createdBy, LocalDateTime.now(), null);
        reward.setOfferedBy(offeredBy);

        when(rewards.findById(any(Long.class))).thenReturn(Optional.of(reward));
        when(usersService.getCurrentUser()).thenReturn(createdBy);
        
        rewardService.deleteReward(reward.getId());

        verify(rewards).findById(reward.getId());
        verify(rewards).delete(reward);
        verify(usersService).getCurrentUser();
    }
}
