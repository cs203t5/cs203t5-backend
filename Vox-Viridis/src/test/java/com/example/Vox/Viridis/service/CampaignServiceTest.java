package com.example.Vox.Viridis.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.CampaignRepository;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @Mock
    private CampaignRepository campaigns;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Test
    void getCampaignCompanyName() {
        Users user = new Users();
        user.setAccount_id(1l);
        user.setEmail("campaign@test.com");
        user.setFirstName("Admin");
        user.setLastName("name");
        user.setUsername("admin123");

        Campaign campaign = new Campaign();
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now().plusDays(1));
        campaign.setEndDate(LocalDateTime.now().plusDays(2));
        campaign.setCreatedBy(user);

        assertEquals(campaign.companyName(), user.getUsername());
    }

    @Test
    void getCampaignStatus_ReturnUpcoming() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now().plusDays(1));
        campaign.setEndDate(LocalDateTime.now().plusDays(2));

        assertEquals(campaign.status(), 'U');
    }

    @Test
    void getCampaignStatus_ReturnOngoing() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(2));

        assertEquals(campaign.status(), 'O');
    }

    @Test
    void getCampaignStatus_ReturnExpired() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now().minusDays(1));
        campaign.setEndDate(LocalDateTime.now().minusMinutes(1));

        assertEquals(campaign.status(), 'E');
    }

    @Test
    void getCampaign_WithoutFilter_ReturnCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");

        Sort sort = Sort.by("created_on").descending().and(Sort.by("title").ascending());
        Pageable pageable = PageRequest.of(0, 20, sort);

        Page<Campaign> page = new PageImpl<>(List.of(campaign));
        when(campaigns.findByTitleAndCategoryAndLocationAndReward("", null, null, null, pageable)).thenReturn(page);

        List<Campaign> result = campaignService.getCampaign(0, null, null, null, null, true);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), campaign);
        verify(campaigns).findByTitleAndCategoryAndLocationAndReward("", null, null, null, pageable);
    }

    @Test
    void getCampaign_WithFilter_ReturnCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");

        Sort sort = Sort.by("created_on").descending().and(Sort.by("title").ascending());
        Pageable pageable = PageRequest.of(0, 20, sort);

        when(campaigns.findByTitleAndCategoryAndLocationAndReward("New", null, null, null, pageable)).thenReturn(new PageImpl<>(List.of(campaign)));

        List<Campaign> result = campaignService.getCampaign(0, "New", null, null, null, true);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), campaign);
        verify(campaigns).findByTitleAndCategoryAndLocationAndReward("New", null, null, null, pageable);
    }

    @Test
    void addCampaign_NewTitle_ReturnSavedCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        when(campaigns.findByTitle(any(String.class))).thenReturn(new ArrayList<Campaign>());
        when(campaigns.save(any(Campaign.class))).thenReturn(campaign);

        Campaign savedCampaign = campaignService.addCampaign(campaign);

        assertNotNull(savedCampaign);
        verify(campaigns).findByTitle(campaign.getTitle());
        verify(campaigns).save(campaign);
    }

    @Test
    void addCampaign_SameTitle_ReturnNull() {
        Campaign campaign = new Campaign();
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        when(campaigns.findByTitle(any(String.class))).thenReturn(List.of(campaign));

        Campaign savedCampaign = campaignService.addCampaign(campaign);

        assertNull(savedCampaign);
        verify(campaigns).findByTitle(campaign.getTitle());
    }

    @Test
    void updateCampaign_NewTitle_ReturnSavedCampaign() {
        Campaign campaign = new Campaign();
        campaign.setId(2l);
        campaign.setTitle("New Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setTitle("New New Campaign");
        updatedCampaign.setStartDate(LocalDateTime.now());
        updatedCampaign.setEndDate(LocalDateTime.now().plusDays(1));

        when(campaigns.findByTitle(any(String.class))).thenReturn(new ArrayList<Campaign>());
        when(campaigns.findById(2l)).thenReturn(Optional.of(campaign));
        when(campaigns.save(any(Campaign.class))).thenReturn(updatedCampaign);

        Campaign savedCampaign = campaignService.updateCampaign(updatedCampaign, 2l);

        assertEquals(savedCampaign, updatedCampaign);
        verify(campaigns).findByTitle(updatedCampaign.getTitle());
        verify(campaigns).findById(2l);
        updatedCampaign.setId(2l);
        verify(campaigns).save(updatedCampaign);
    }

    @Test
    void updateCampaign_SaneTitle_ReturnSavedCampaign() {
        Campaign campaign = new Campaign();
        campaign.setId(2l);
        campaign.setTitle("New Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setTitle("New Campaign");
        updatedCampaign.setStartDate(LocalDateTime.now());
        updatedCampaign.setEndDate(LocalDateTime.now().plusDays(1));

        when(campaigns.findByTitle(any(String.class))).thenReturn(List.of(campaign));
        when(campaigns.findById(2l)).thenReturn(Optional.of(campaign));
        when(campaigns.save(any(Campaign.class))).thenReturn(updatedCampaign);

        Campaign savedCampaign = campaignService.updateCampaign(updatedCampaign, 2l);

        assertEquals(savedCampaign, updatedCampaign);
        verify(campaigns).findByTitle(updatedCampaign.getTitle());
        verify(campaigns).findById(2l);
        updatedCampaign.setId(2l);
        verify(campaigns).save(updatedCampaign);
    }

    @Test
    void updateCampaign_SameTitle_ReturnNull() {
        Campaign campaign = new Campaign();
        campaign.setId(1l);
        campaign.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        Campaign campaign2 = new Campaign();
        campaign2.setTitle("Existing Campaign");
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        when(campaigns.findByTitle(any(String.class))).thenReturn(List.of(campaign));

        Campaign savedCampaign = campaignService.updateCampaign(campaign2, 2l);

        assertNull(savedCampaign);
        verify(campaigns).findByTitle(campaign.getTitle());
    }
}
