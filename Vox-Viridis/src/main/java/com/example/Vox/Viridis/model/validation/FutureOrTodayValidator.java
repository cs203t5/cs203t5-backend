package com.example.Vox.Viridis.model.validation;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FutureOrTodayValidator implements ConstraintValidator<FutureOrToday, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) return false;
        //if (value.toLocalTime() == null || value.toLocalTime().equals(LocalTime.of(0,0))) {
            return value.toLocalDate().compareTo(LocalDate.now()) >= 0;
        //}
        //return value.compareTo(LocalDateTime.now()) >= 0;
    }
    
}
