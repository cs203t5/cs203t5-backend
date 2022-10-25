package com.example.Vox.Viridis.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.Vox.Viridis.exception.ResourceNotFoundException;
import com.example.Vox.Viridis.model.Participation;
import com.example.Vox.Viridis.service.ParticipationService;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("participation")
@RequiredArgsConstructor
public class ParticipationController {
    private final ParticipationService participationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Participation createParticipation(@RequestParam("rewardId") long rewardId) {
        Participation participation = participationService.createParticipation(rewardId);
        if (participation == null) 
            throw new ResourceNotFoundException("Reward id " + rewardId);
        return participation;
    }

    @PostMapping("addPoints/{id}")
    public Participation addPoints(@PathVariable Long id, 
        @RequestParam("noOfStamp") @Valid @Min(value = 1, message = "noOfStamp must be at least 1") int noOfStamp) {
        return participationService.addNoOfStamps(id, noOfStamp);
    }
}
