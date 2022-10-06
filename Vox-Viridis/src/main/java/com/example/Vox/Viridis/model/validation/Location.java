package com.example.Vox.Viridis.model.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * If LocalDateTime has time = 00:00, then will be treated as date only (without time)
 * Else, will validate both date and time is after today's date and time
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LocationValidator.class)
public @interface Location {
    String message() default "Invalid location: either North, South, East, West, or Central";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
