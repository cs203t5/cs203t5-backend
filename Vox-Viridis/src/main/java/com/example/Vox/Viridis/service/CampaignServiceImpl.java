package com.example.Vox.Viridis.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.exception.NotOwnerException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.repository.CampaignRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final UsersService usersService;

    @Override
    public Optional<Campaign> getCampaign(Long id) {
        return campaignRepository.findById(id);
    }

    public Campaign addCampaign(Campaign campaign) {
        String title = campaign.getTitle();
        if (!campaignRepository.findByTitle(title).isEmpty()) {
            log.error("Error creating Campaign: duplicate title: " + title);
            return null;
        }

        log.info("Campaign created: " + title);
        campaign.setCreatedOn(LocalDateTime.now());
        campaign.setCreatedBy(usersService.getCurrentUser());
        return campaignRepository.save(campaign);
    }

    @Override
    public List<Campaign> getCampaign(int page, String filterByTitle, List<String> category,
            List<String> location, List<String> reward, boolean isOrderByNewest) {
        Sort sort = Sort.by("createdOn");
        if (isOrderByNewest)
            sort = sort.descending();
        else
            sort = sort.ascending();
        sort = sort.and(Sort.by("title").ascending());

        Pageable pageable = PageRequest.of(page, 20, sort);
        if (filterByTitle == null)
            filterByTitle = "";
        List<Campaign> campaigns =
                campaignRepository.findByTitleAndCategoryAndLocationAndReward(filterByTitle,
                        category, location, reward, pageable).getContent();
        return campaigns;
    }

    @Override
    public Campaign updateCampaignImage(Campaign campaign, String imageFilename) {
        campaign.setImage(imageFilename);
        log.info("updated campaign image to '" + imageFilename + "'' for id: " + campaign.getId());
        return campaignRepository.save(campaign);
    }

    /**
     * @Return null if title alr exists. Else, return updated campaign
     * @throws CampaignNotFoundException if campaign doesn't exist
     * @throws NotOwnerException if current user isn't the owner of this campaign
     */
    @Override
    public Campaign updateCampaign(Campaign updatedCampaign, Long id) {
        List<Campaign> tmp = campaignRepository.findByTitle(updatedCampaign.getTitle());
        if (!((tmp.size() == 1 && tmp.get(0).getId() == id) || tmp.size() == 0)) {
            log.error("Error creating Campaign: duplicate title: " + updatedCampaign.getTitle());
            return null;
        }

        Campaign existingCampaign = getCampaign(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));
        Users username = usersService.getCurrentUser();
        if (username != null && !existingCampaign.getCreatedBy().equals(username))
            throw new NotOwnerException();

        updatedCampaign.setId(id);
        updatedCampaign.setImage(existingCampaign.getImage());
        updatedCampaign.setCreatedBy(existingCampaign.getCreatedBy());
        updatedCampaign.setCreatedOn(existingCampaign.getCreatedOn());
        log.info("updated campaign id: " + id);
        return campaignRepository.save(updatedCampaign);
    }

    /**
     * @throws NotOwnerException if current user isn't the owner of this campaign
     */
    @Override
    public void deleteCampaign(Long id) {
        Users username = usersService.getCurrentUser();
        if (username != null
                && !campaignRepository.getCreatedBy(id).equals(username.getAccountId()))
            throw new NotOwnerException();
        log.info("Delete campaign id: " + id);
        campaignRepository.deleteById(id);
    }
}
