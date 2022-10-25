package com.example.Vox.Viridis.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.exception.ParticipationAlreadyJoinedException;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.service.ParticipationService;
import com.example.Vox.Viridis.service.StorageService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("participation")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService participationService;
    private final StorageService storageService;

    @GetMapping
    public List<Participation> getMyParticipation() {
        return participationService.getMyParticipation();
    }

    @GetMapping("myPoints")
    public int getMyPoints() {
        return participationService.getMyPoints();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{rewardId}")
    public Participation createParticipation(@PathVariable long rewardId) {
        Participation participation = participationService.createParticipation(rewardId);
        if (participation == null) 
            throw new ParticipationAlreadyJoinedException(rewardId);
        participation.getReward().constructCampaignImage(storageService);
        return participation;
    }

    @PostMapping("addPoints/{id}")
    public Participation addPoints(@PathVariable Long id, 
        @RequestBody @Valid ParticipationAddPointInputModel input) {
        return participationService.addNoOfStamps(id, input.getNoOfStamp());
    }
}

@Data
class ParticipationAddPointInputModel {
    @Min(value = 1, message = "noOfStamp must be at least 1") 
    private int noOfStamp;
}
