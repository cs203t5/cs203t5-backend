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

import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.repository.CampaignRepository;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @Mock
    private CampaignRepository campaigns;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    @Test
    void getCampaign_WithoutFilter_ReturnCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");

        when(campaigns.findTop20ByTitleOrderByTitleAsc(any(String.class))).thenReturn(List.of(campaign));

        List<Campaign> result = campaignService.getCampaign((String)null);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), campaign);
        verify(campaigns).findTop20ByTitleOrderByTitleAsc("");
    }

    @Test
    void getCampaign_WithFilter_ReturnCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");

        when(campaigns.findTop20ByTitleOrderByTitleAsc(any(String.class))).thenReturn(List.of(campaign));

        List<Campaign> result = campaignService.getCampaign("New");

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), campaign);
        verify(campaigns).findTop20ByTitleOrderByTitleAsc("New");
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
        campaign.setGoal(1);
        campaign.setStartDate(LocalDateTime.now());
        campaign.setEndDate(LocalDateTime.now().plusDays(1));

        Campaign updatedCampaign = new Campaign();
        updatedCampaign.setTitle("New Campaign");
        updatedCampaign.setGoal(2);
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
