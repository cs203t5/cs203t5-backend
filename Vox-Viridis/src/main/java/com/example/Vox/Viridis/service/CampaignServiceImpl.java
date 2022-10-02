package com.example.Vox.Viridis.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.CampaignNotFoundException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    
    public boolean validateCampaign(Campaign campaign) {
        if (campaign.getEndDate().isBefore(campaign.getStartDate())) return false;
        return true;
    }

    @Override
    public Optional<Campaign> getCampaign(Long id) {
        return campaignRepository.findById(id);
    }

    public Campaign addCampaign(Campaign campaign) {
        if (!validateCampaign(campaign)) return null;
        
        String title = campaign.getTitle();
        if (!campaignRepository.findByTitle(title).isEmpty()) {
            log.error("Error creating Campaign: duplicate title: " + title);
            return null;
        }

        log.info("Campaign created: " + title);
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaign(String filterByTitle) {
        if (filterByTitle == null) filterByTitle = "";
        return campaignRepository.findTop20ByTitleOrderByTitleAsc(filterByTitle);
    }

    @Override
    public Campaign updateCampaignImage(Campaign campaign, String imageFilename) {
        campaign.setImage(imageFilename);
        return campaignRepository.save(campaign);
    }

    /// Return null if title alr exists
    /// @Throw CampaignNotFoundException
    @Override
    public Campaign updateCampaign(Campaign updatedCampaign, Long id) {
        List<Campaign> tmp = campaignRepository.findByTitle(updatedCampaign.getTitle());
        if ((tmp.size() == 1 && tmp.get(0).getId() == id)) return null;

        Campaign existingCampaign = getCampaign(id).orElseThrow(() -> new CampaignNotFoundException(id));

        updatedCampaign.setId(id);
        updatedCampaign.setImage(existingCampaign.getImage());
        
        return campaignRepository.save(updatedCampaign);
    }

    @Override
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);;
    }
}
