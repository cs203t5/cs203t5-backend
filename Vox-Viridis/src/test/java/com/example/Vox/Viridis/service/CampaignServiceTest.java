package com.example.Vox.Viridis.service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

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
    void addCampaign_NewTitle_ReturnSavedCampaign() {
        Campaign campaign = new Campaign();
        campaign.setTitle("New Campaign");

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

        when(campaigns.findByTitle(any(String.class))).thenReturn(List.of(campaign));

        Campaign savedCampaign = campaignService.addCampaign(campaign);

        assertNull(savedCampaign);
        verify(campaigns).findByTitle(campaign.getTitle());
    }
}
