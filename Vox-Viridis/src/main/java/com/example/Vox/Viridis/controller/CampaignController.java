package com.example.Vox.Viridis.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Vox.Viridis.exception.CampaignExistsException;
import com.example.Vox.Viridis.exception.InvalidFileTypeException;
import com.example.Vox.Viridis.model.Campaign;
import com.example.Vox.Viridis.service.AwsS3Storage;
import com.example.Vox.Viridis.service.CampaignService;
import com.example.Vox.Viridis.service.StorageService;

import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("campaign")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;
    private final StorageService storageService;

    @PostMapping()
    public Campaign addCampaign(@ModelAttribute @Valid Campaign campaign, @RequestParam(value="imageFile", required=false) MultipartFile image) {
        Campaign result = campaignService.addCampaign(campaign);
        if (result == null) throw new CampaignExistsException(campaign.getTitle());
        
        if (!image.isEmpty()) {
            if (image.getContentType() == null || !image.getContentType().startsWith("image/"))
                throw new InvalidFileTypeException("Image like jpeg");
            
            storageService.putObject(AwsS3Storage.CAMPAIGNS_DIR + result.getId() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")), image);
        }

        return result;
    }
}
