package com.autumnflix.streaming.core.validation.validator;

import com.autumnflix.streaming.core.validation.annotation.MinimumAgeConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class MinimumAgeValidator implements ConstraintValidator<MinimumAgeConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate minimumAge = LocalDate.now().minusYears(18);

        if(dob == null){
            return false;
        }
        else{
            return dob.isBefore(minimumAge);
        }
    }
}
