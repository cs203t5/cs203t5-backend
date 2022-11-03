package com.example.Vox.Viridis.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardType;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.repository.ParticipationRepository;

@ExtendWith(MockitoExtension.class)
public class ParticipationServiceTest {
    @Mock
    private ParticipationRepository participations;

    @Mock
    private UsersService usersService;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private ParticipationService participationService;

    private static Users createCurrentUser() {
        Users currentUser = new Users();
        currentUser.setAccountId(1l);
        currentUser.setEmail("user@test.com");
        currentUser.setFirstName("user");
        currentUser.setLastName("name");
        currentUser.setUsername("admin123");
        return currentUser;
    }

    private static Users createAdminUser() {
        Users currentUser = new Users();
        currentUser.setAccountId(2l);
        currentUser.setEmail("admin@test.com");
        currentUser.setFirstName("admin");
        currentUser.setLastName("name");
        currentUser.setUsername("admin123");
        return currentUser;
    }

    private static Campaign createCampaign(Users createdBy) {
        return new Campaign(1l, 
        "Campaign title", "campaign description", 
        LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), 
        "North", "SMU address", null, "Plastic", null, createdBy, LocalDateTime.now(), null);
    }

    private static RewardType createRewardTypePoints() {
        return new RewardType(1l, "Points", null);
    }
    private static RewardType createRewardTypeCards() {
        return new RewardType(1l, "Cards", null);
    }

    @Test
    void getMyParticipation_ReturnMyParticipation() {
        Users currentUser = createCurrentUser();

        Users createdBy = createAdminUser();
        Campaign campaign1 = createCampaign(createdBy);
        RewardType rewardType = createRewardTypePoints();
        Reward reward1 = new Reward(1l, "Reward test", campaign1, rewardType, 10, "tnc", null);
        rewardType.setRewards(List.of(reward1));
        campaign1.setRewards(reward1);
        Participation participation = new Participation(1l, 0, LocalDateTime.now(), reward1, currentUser);
        reward1.setParticipations(List.of(participation));

        Pageable pageable = PageRequest.of(0, 20);

        when(participations.findByUser(currentUser, pageable)).thenReturn(new PageImpl<>(List.of(
           participation 
        )));
        when(usersService.getCurrentUser()).thenReturn(currentUser);

        PaginationDTO<Participation> result = participationService.getMyParticipation(0);

        assertNotNull(result);
        verify(participations).findByUser(currentUser, pageable);
        verify(usersService).getCurrentUser();
    }

    @Test
    void getMyPoints_ReturnMyTotalPoints() {
        final int MY_POINTS = 10;

        Users currentUser = createCurrentUser();
        currentUser.setPoints(MY_POINTS);

        when(usersService.getCurrentUser()).thenReturn(currentUser);

        int result = participationService.getMyPoints();

        assertEquals(MY_POINTS, result);
        verify(usersService).getCurrentUser();
    }

    // New Participation (doesn't exist for current user)
    @Test
    void createParticipation_NewReward_ReturnCreatedParticipation() {
        Users currentUser = createCurrentUser();
        Users createdBy = createAdminUser();
        Campaign campaign1 = createCampaign(createdBy);
        Reward reward1 = new Reward(1l, "Reward", campaign1, createRewardTypePoints(), 10, "tnc", null);

        when(usersService.getCurrentUser()).thenReturn(currentUser);
        when(rewardService.getReward(1)).thenReturn(Optional.of(reward1));
        when(participations.findByRewardAndUser(reward1, currentUser)).thenReturn(Optional.empty());
        when(participations.save(any(Participation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Participation result = participationService.createParticipation(1l);

        assertNotNull(result);
        assertEquals(currentUser, result.getUser());
        assertEquals(reward1, result.getReward());
        verify(usersService).getCurrentUser();
        verify(rewardService).getReward(1l);
        verify(participations).findByRewardAndUser(reward1, currentUser);
        verify(participations).save(any(Participation.class));
    }

    @Test
    void createParticipation_AlreadyExist_ReturnNull() {
        Users currentUser = createCurrentUser();
        Users createdBy = createAdminUser();
        Campaign campaign1 = createCampaign(createdBy);
        Reward reward1 = new Reward(1l, "Reward", campaign1, createRewardTypePoints(), 10, "tnc", null);

        when(usersService.getCurrentUser()).thenReturn(currentUser);
        when(rewardService.getReward(1)).thenReturn(Optional.of(reward1));
        when(participations.findByRewardAndUser(reward1, currentUser)).thenReturn(
            Optional.of(new Participation(1l, 0, LocalDateTime.now(), reward1, currentUser)));
        
        Participation result = participationService.createParticipation(1l);

        assertNull(result);
        verify(usersService).getCurrentUser();
        verify(rewardService).getReward(1l);
        verify(participations).findByRewardAndUser(reward1, currentUser);
    }

    @Test
    void addNoOfStamps_Points_ReturnUpdatedParticipation() {
        Users currentUser = createCurrentUser();
        final int GOAL = 10;
        Reward reward1 = new Reward(1l, "Reward", createCampaign(createAdminUser()), createRewardTypePoints(), GOAL, "tnc", null);
        Participation participation = new Participation(1l, 0, LocalDateTime.now(), reward1, currentUser);

        when(participations.findById(1l)).thenReturn(Optional.of(participation));
        when(usersService.updateUser(currentUser)).thenAnswer(invocation -> ((Users)invocation.getArgument(0)).convertToDTO());
        
        Participation result = participationService.addNoOfStamps(1l, 30);

        assertNotNull(result);
        assertEquals(GOAL, currentUser.getPoints()); // assume at first, currentUser have 0 points
        assertEquals(0, result.getNoOfStamp()); // should remain 0 (no change)
        verify(participations).findById(1l);
        verify(usersService).updateUser(currentUser);
    }

    @Test
    void addNoOfStamps_Cards_ReturnUpdatedParticipation() {
        Users currentUser = createCurrentUser();
        final int GOAL = 10;
        final int ADD_STAMP = 5;
        Reward reward1 = new Reward(1l, "Reward", createCampaign(createAdminUser()), createRewardTypeCards(), GOAL, "tnc", null);
        Participation participation = new Participation(1l, 0, LocalDateTime.now(), reward1, currentUser);

        when(participations.findById(1l)).thenReturn(Optional.of(participation));
        when(participations.save(any(Participation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Participation result = participationService.addNoOfStamps(1l, ADD_STAMP);

        assertNotNull(result);
        assertEquals(0, currentUser.getPoints()); // should remain 0 (no change)
        assertEquals(ADD_STAMP, result.getNoOfStamp());
        verify(participations).findById(1l);
        verify(participations).save(any(Participation.class));
    }
}
