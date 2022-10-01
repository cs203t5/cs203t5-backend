package com.example.Vox.Viridis.service;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
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
    
    public Campaign addCampaign(Campaign campaign) {
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
}
