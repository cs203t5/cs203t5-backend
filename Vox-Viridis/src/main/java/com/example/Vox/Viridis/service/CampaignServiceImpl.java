package com.example.Vox.Viridis.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.exception.NoGreenWordException;
import com.example.Vox.Viridis.exception.NotOwnerException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Users;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.repository.CampaignRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CampaignServiceImpl implements CampaignService {
    private final Resource resource;
    private final CampaignRepository campaignRepository;
    private final UsersService usersService;

    public CampaignServiceImpl(CampaignRepository campaignRepository, UsersService usersService, @Value("classpath:WHITELISTED_WORDS.txt") Resource resource) {
        this.campaignRepository = campaignRepository;
        this.usersService = usersService;
        this.resource = resource;
    }

    @Override
    public Optional<Campaign> getCampaign(Long id) {
        return campaignRepository.findById(id);
    }

    public Campaign addCampaign(Campaign campaign) {
        String title = campaign.getTitle();
        String description = campaign.getDescription();
        if (!campaignRepository.findByTitle(title).isEmpty()) {
            log.error("Error creating Campaign: duplicate title: " + title);
            return null;
        }

        // check if contain green (whitelisted) words
        try (Scanner scanner = new Scanner(resource.getFile())){
            boolean doesContain = false;
            final String titleLowerCase = title.toLowerCase();
            final String descLowerCase = description == null ? description : description.toLowerCase();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                if (titleLowerCase.contains(line) || (descLowerCase != null && descLowerCase.contains(line))) {
                    doesContain = true;
                    break;
                }
            }
            if (!doesContain) {
                log.error(
                        "Error creating Campaign: title and description does not contain any of the whitelisted words");
                throw new NoGreenWordException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        

        log.info("Campaign created: " + title);
        campaign.setCreatedOn(LocalDateTime.now());
        campaign.setCreatedBy(usersService.getCurrentUser());
        return campaignRepository.save(campaign);
    }

    @Override
    public PaginationDTO<Campaign> getCampaign(int page, String filterByTitle,
            List<String> category, List<String> location, List<String> reward,
            boolean isOrderByNewest) {
        Sort sort = Sort.by("createdOn");
        if (isOrderByNewest)
            sort = sort.descending();
        else
            sort = sort.ascending();
        sort = sort.and(Sort.by("title").ascending());

        Pageable pageable = PageRequest.of(page, 20, sort);
        if (filterByTitle == null)
            filterByTitle = "";

        Page<Campaign> campaigns = campaignRepository.findByTitleAndCategoryAndLocationAndReward(
                filterByTitle, category, location, reward, pageable);
        return new PaginationDTO<>(campaigns);
    }

    /**
     * Will return a list of campaigns created by current user
     * 
     * @return list of campaign created by current user
     */
    @Override
    public List<Campaign> getCampaignCreatedByCurrentUser() {
        return campaignRepository.findByCreatedBy(usersService.getCurrentUser());
    }

    @Override
    public Campaign updateCampaignImage(Campaign campaign, String imageFilename) {
        campaign.setImage(imageFilename);
        log.info("updated campaign image to '" + imageFilename + "' for id: " + campaign.getId());
        return campaignRepository.save(campaign);
    }

    /**
     * @Return null if title alr exists. Else, return updated campaign
     * @throws CampaignNotFoundException if campaign doesn't exist
     * @throws NotOwnerException if current user isn't the owner of this campaign
     */
    @Override
    public Campaign updateCampaign(Campaign updatedCampaign, Long id) {
        Campaign existingCampaign = getCampaign(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));

        // Check if there's duplicate title
        List<Campaign> tmp = campaignRepository.findByTitle(updatedCampaign.getTitle());
        if (!((tmp.size() == 1 && tmp.get(0).getId().equals(id)) || tmp.isEmpty())) {
            log.error("Error creating Campaign: duplicate title: " + updatedCampaign.getTitle());
            return null;
        }

        try (Scanner scanner = new Scanner(resource.getFile())){
            boolean doesContain = false;
            final String titleLowerCase = updatedCampaign.getTitle().toLowerCase();
            final String descLowerCase = updatedCampaign.getDescription() == null ? null : updatedCampaign.getDescription().toLowerCase();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                if (titleLowerCase.contains(line) || (descLowerCase != null && descLowerCase.contains(line))) {
                    doesContain = true;
                    break;
                }
            }
            if (!doesContain) {
                log.error(
                        "Error creating Campaign: title and description does not contain any of the whitelisted words");
                throw new NoGreenWordException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Users username = usersService.getCurrentUser();
        if (!existingCampaign.getCreatedBy().equals(username))
            throw new NotOwnerException();

        updatedCampaign.setId(id);
        updatedCampaign.setImage(existingCampaign.getImage());
        updatedCampaign.setCreatedBy(existingCampaign.getCreatedBy());
        updatedCampaign.setCreatedOn(existingCampaign.getCreatedOn());
        updatedCampaign.setRewards(existingCampaign.getRewards());
        log.info("updated campaign id: " + id);
        return campaignRepository.save(updatedCampaign);
    }

    /**
     * @throws NotOwnerException if current user isn't the owner of this campaign
     */
    @Override
    public void deleteCampaign(Long id) {
        Users username = usersService.getCurrentUser();
        if (!campaignRepository.getCreatedBy(id).equals(username.getAccountId()))
            throw new NotOwnerException();
        log.info("Delete campaign id: " + id);
        campaignRepository.deleteById(id);
    }
}
