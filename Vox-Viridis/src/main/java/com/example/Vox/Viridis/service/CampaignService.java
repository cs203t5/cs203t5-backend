package com.example.Vox.Viridis.service;

import java.util.List;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignService {
    List<Campaign> getCampaign(String filterByTitle);
    Campaign addCampaign(Campaign campaign);
}
