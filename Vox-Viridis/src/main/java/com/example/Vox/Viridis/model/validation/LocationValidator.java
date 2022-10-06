package com.example.Vox.Viridis.model.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationValidator implements ConstraintValidator<Location, String> {
    private final String[] VALID_STRINGS = {"North", "South", "East", "West", "Central"};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;
        for (String str : VALID_STRINGS) {
            if (str.equalsIgnoreCase(value)) return true;
        }
        return false;
    }
    
}
