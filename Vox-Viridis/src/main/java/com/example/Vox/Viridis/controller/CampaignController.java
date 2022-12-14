package com.example.Vox.Viridis.controller;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.exception.CampaignTitleExistsException;
import com.example.Vox.Viridis.exception.InvalidFileTypeException;
import com.example.Vox.Viridis.exception.InvalidJsonException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardInputModel;
import com.example.Vox.Viridis.model.dto.PaginationDTO;
import com.example.Vox.Viridis.service.CampaignService;
import com.example.Vox.Viridis.service.RewardService;
import com.example.Vox.Viridis.service.RewardTypeService;
import com.example.Vox.Viridis.service.StorageService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;
    private final StorageService storageService;
    private final RewardTypeService rewardTypeService;
    private final RewardService rewardService;
    private final Validator validator;

    @GetMapping("{id}")
    public Campaign getCampaign(@PathVariable Long id) {
        Campaign result = campaignService.getCampaign(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));
        result.constructImageUrl(storageService);
        return result;
    }

    @GetMapping("myCampaign")
    public List<Campaign> getMyCampaign() {
        List<Campaign> result = campaignService.getCampaignCreatedByCurrentUser();
        result.forEach(campaign -> 
            campaign.constructImageUrl(storageService)
        );
        return result;
    }

    @GetMapping()
    public PaginationDTO<Campaign> getCampaign(@RequestParam(value = "filterByTitle", required = false) String filterByTitle,
            @RequestParam(value = "category", required = false) List<String> category,
            @RequestParam(value = "location", required = false) List<String> location,
            @RequestParam(value = "reward", required = false) List<String> reward,
            @RequestParam(value = "isOrderByNewest", required = false) Boolean isOrderByNewest,
            @RequestParam(value = "pageNum", required = false) Integer pageNum) {
        if (isOrderByNewest == null)
            isOrderByNewest = true;
        if (pageNum == null)
            pageNum = 0;
        PaginationDTO<Campaign> result = campaignService.getCampaign(pageNum, filterByTitle, category, location, reward,
                isOrderByNewest);

        result.getElements().forEach(campaign -> 
            campaign.constructImageUrl(storageService)
        );
        return result;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    @Transactional
    public Campaign addCampaign(@ModelAttribute @Valid Campaign campaign,
            @RequestParam(value = "imageFile", required = false) MultipartFile image,
            @RequestParam(value = "reward", required = false) String rewardJson) {
        if (image != null && !image.isEmpty()) {
            final String imageContentType = image.getContentType();
            if (imageContentType == null || !imageContentType.startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }

        Campaign result = campaignService.addCampaign(campaign);
        if (result == null) throw new CampaignTitleExistsException(campaign.getTitle());

        // create array of rewards
        if (rewardJson != null) {
            try {
                JSONObject rewardJsonObj = new JSONObject(rewardJson);
                String rewardName = rewardJsonObj.getString("rewardName");
                String rewardTypeName = rewardJsonObj.getString("rewardType");
                Integer goal;
                if (!rewardJsonObj.has("goal"))
                    goal = null;
                else
                    goal = rewardJsonObj.getInt("goal");
                String tnc = rewardJsonObj.getString("tnc");
                RewardInputModel rewardInput = new RewardInputModel(rewardTypeName, rewardName, goal, tnc);

                Set<ConstraintViolation<RewardInputModel>> constraintViolation = validator.validate(rewardInput);
                if (!constraintViolation.isEmpty())
                    throw new ConstraintViolationException(constraintViolation);

                Reward reward = rewardInput.convertToReward(rewardTypeService);
                reward = rewardService.addReward(reward, result);
                result.setRewards(reward);
            } catch (JSONException e) {
                throw new InvalidJsonException("reward", e);
            }
        }

        if (image != null && !image.isEmpty()) {
            String imageOriginalFilename = image.getOriginalFilename();
            if (imageOriginalFilename == null)
                imageOriginalFilename = "";
            String filename = StorageService.CAMPAIGNS_DIR + result.getId()
                    + imageOriginalFilename.substring(imageOriginalFilename.lastIndexOf("."));
            result = campaignService.updateCampaignImage(campaign, filename);

            storageService.putObject(filename, image);
        }

        result.constructImageUrl(storageService);

        return result;
    }

    @Transactional
    @PutMapping("{id}")
    public Campaign updateCampaign(@PathVariable Long id, @ModelAttribute @Valid Campaign campaign,
            @RequestParam(value = "imageFile", required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            final String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }

        Campaign result = campaignService.updateCampaign(campaign, id);
        if (result == null) throw new CampaignTitleExistsException(campaign.getTitle());

        if (image != null && !image.isEmpty()) {
            if (result.getImage() != null)
                storageService.deleteObject(result.getImage());

            String imageOriginalFilename = image.getOriginalFilename();
            if (imageOriginalFilename == null)
                imageOriginalFilename = "";
            String filename = StorageService.CAMPAIGNS_DIR + result.getId()
                    + imageOriginalFilename.substring(imageOriginalFilename.lastIndexOf("."));
            result = campaignService.updateCampaignImage(campaign, filename);

            storageService.putObject(filename, image);
        }

        result.constructImageUrl(storageService);
        return result;
    }

    @DeleteMapping("{id}")
    public void deleteCampaign(@PathVariable Long id) {
        Campaign campaign = campaignService.getCampaign(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));
        if (campaign.getImage() != null && !campaign.getImage().isBlank())
            storageService.deleteObject(campaign.getImage());
        campaignService.deleteCampaign(id);
    }
}