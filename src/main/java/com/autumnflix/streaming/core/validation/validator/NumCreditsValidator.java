package com.autumnflix.streaming.core.validation.validator;

import com.autumnflix.streaming.api.model.plan.PlanInputDTO;
import com.autumnflix.streaming.core.validation.annotation.NumCreditsConstraint;
import com.autumnflix.streaming.domain.model.PlanType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumCreditsValidator implements ConstraintValidator<NumCreditsConstraint, PlanInputDTO> {

    private String message;
    private String premiumMessage;

    @Override
    public void initialize(NumCreditsConstraint constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.premiumMessage = constraintAnnotation.premiumMessage();
    }

    @Override
    public boolean isValid(PlanInputDTO planInputDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (!planInputDTO.getType().equals(PlanType.PREMIUM)) {
            if (planInputDTO.getNumCredits() != null && planInputDTO.getNumCredits() > 0) {
                return true;
            } else {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
        } else {
            if (planInputDTO.getNumCredits() == null) {
                return true;
            } else {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(premiumMessage).addConstraintViolation();
                return false;
            }
        }
    }
}
