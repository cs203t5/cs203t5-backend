package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import com.example.Vox.Viridis.model.Campaign;

public interface CampaignService {
    Optional<Campaign> getCampaign(Long id);
    List<Campaign> getCampaign(int page, String filterByTitle, List<String> category, List<String> location, List<String> reward, boolean isOrderByNewest);
    Campaign addCampaign(Campaign campaign);
    Campaign updateCampaignImage(Campaign campaign, String imageFilename);
    Campaign updateCampaign(Campaign updatedCampaign, Long id);
    void deleteCampaign(Long id);
}
