package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignService {
    Optional<Campaign> getCampaign(Long id);
    List<Campaign> getCampaign(String filterByTitle);
    Campaign addCampaign(Campaign campaign);
    Campaign updateCampaignImage(Campaign campaign, String imageFilename);
    Campaign updateCampaign(Campaign updatedCampaign, Long id);
    void deleteCampaign(Long id);
}
