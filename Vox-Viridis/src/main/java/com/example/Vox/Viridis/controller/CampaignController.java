package com.example.Vox.Viridis.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.json.JSONArray;
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

import com.example.Vox.Viridis.exception.CampaignTitleExistsException;
import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.exception.InvalidFileTypeException;
import com.example.Vox.Viridis.exception.InvalidJsonException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.model.Reward;
import com.example.Vox.Viridis.model.RewardType;
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
    private final EntityManager entityManager;
    private final RewardTypeService rewardTypeService;
    private final RewardService rewardService;

    @GetMapping("{id}")
    public Campaign getCampaign(@PathVariable Long id){
        Campaign result =  campaignService.getCampaign(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));
        entityManager.detach(result);
        String image = result.getImage();
        if (image != null)
            result.setImage(storageService.getUrl(image));
        return result;
    }

    @GetMapping()
    public List<Campaign> getCampaign(@RequestParam(value="filterByTitle", required=false) String filterByTitle,
            @RequestParam(value="category", required=false) String category,
            @RequestParam(value="location", required=false) String location,
            @RequestParam(value="isOrderByNewest", required=false) Boolean isOrderByNewest,
            @RequestParam(value="pageNum", required=false) Integer pageNum){
        if (isOrderByNewest == null) isOrderByNewest = true;
        if (pageNum == null) pageNum = 0;
        List<Campaign> result = campaignService.getCampaign(pageNum, filterByTitle, category, location, isOrderByNewest);
        
        result.forEach(campaign -> {
                String image = campaign.getImage();
                if (image != null) {
                    entityManager.detach(campaign);
                    campaign.setImage(storageService.getUrl(image));
                }
            });
        return result;
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public Campaign addCampaign(@ModelAttribute @Valid Campaign campaign, 
            @RequestParam(value="imageFile", required=false) MultipartFile image, 
            @RequestParam("rewardsArr")String rewardsJson) {
        if (image != null && !image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }

        Campaign result = campaignService.addCampaign(campaign);
        if (result == null) throw new CampaignTitleExistsException(campaign.getTitle());

        // create array of rewards
        List<Reward> rewardsArr = new ArrayList<>();
        try {
            JSONArray rewardsJsonArr = new JSONArray(rewardsJson);
            for (int i = 0; i < rewardsJsonArr.length(); i++) {
                JSONObject current = rewardsJsonArr.getJSONObject(i);
                String rewardName = current.getString("rewardName");
                String rewardTypeName = current.getString("rewardType");
                RewardType rewardType = rewardTypeService.getRewardTypeByName(rewardTypeName).orElseThrow(() -> new ResourceNotFoundException("Reward type ''" + rewardTypeName + "'"));
                
                Reward reward = new Reward();
                reward.setRewardName(rewardName);
                reward.setRewardType(rewardType);
                rewardsArr.add(reward);
            }
        } catch (JSONException e) {
            throw new InvalidJsonException("rewardsArr", e);
        }
        rewardsArr = rewardService.addReward(rewardsArr, result);
        result.setRewards(rewardsArr);
        
        if (image != null && !image.isEmpty()) {
            String filename = StorageService.CAMPAIGNS_DIR + result.getId() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            result = campaignService.updateCampaignImage(campaign, filename);

            storageService.putObject(filename, image);
        }

        entityManager.detach(result);
        result.setImage(storageService.getUrl(result.getImage()));

        return result;
    }

    @Transactional
    @PutMapping("{id}")
    public Campaign updateCampaign(@PathVariable Long id, @ModelAttribute @Valid Campaign campaign, @RequestParam(value="imageFile", required=false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/"))
                throw new InvalidFileTypeException("Image file like jpeg");
        }

        Campaign result = campaignService.updateCampaign(campaign, id);
        if (result == null) throw new CampaignTitleExistsException(campaign.getTitle());
        
        if (image != null && !image.isEmpty()) {
            if (result.getImage() != null)
                storageService.deleteObject(result.getImage());

            String filename = StorageService.CAMPAIGNS_DIR + result.getId() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
            result = campaignService.updateCampaignImage(campaign, filename);

            storageService.putObject(filename, image);
        }

        entityManager.detach(result);
        result.setImage(storageService.getUrl(result.getImage()));

        return result;
    }

    @DeleteMapping("{id}")
    public void deleteCampaign(@PathVariable Long id){
        Campaign campaign = campaignService.getCampaign(id).orElseThrow(() -> new ResourceNotFoundException("Campaign id " + id));
        if (campaign.getImage() != null && !campaign.getImage().isBlank())
            storageService.deleteObject(campaign.getImage());
        campaignService.deleteCampaign(id);
    }
}
