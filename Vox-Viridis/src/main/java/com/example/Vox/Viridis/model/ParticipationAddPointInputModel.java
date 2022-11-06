package com.example.Vox.Viridis.model;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationAddPointInputModel {
    @Min(value = 1, message = "noOfStamp must be at least 1") 
    private int noOfStamp;
}
