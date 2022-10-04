package com.example.Vox.Viridis.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.Vox.Viridis.model.Campaign;

public class ConsistentDateValidator implements ConstraintValidator<ConsistentDate, Campaign> {

    @Override
    public boolean isValid(Campaign value, ConstraintValidatorContext context) {
        if (value == null || value.getStartDate() == null || value.getEndDate() == null) return false;
        return !value.getEndDate().isBefore(value.getStartDate());
    }
    
}
