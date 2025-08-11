package com.autumnflix.streaming.core.validation.validator;

import com.autumnflix.streaming.core.validation.annotation.ReleaseYearConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ReleaseYearValidator implements ConstraintValidator<ReleaseYearConstraint, Year> {
    @Override
    public void initialize(ReleaseYearConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Year year, ConstraintValidatorContext constraintValidatorContext) {

        if (year == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("não deve ser nulo")
                    .addConstraintViolation();
            return false;
        }

        return !year.isBefore(Year.of(1895));
    }
}
