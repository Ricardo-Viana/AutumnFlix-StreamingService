package com.autumnflix.streaming.core.validation.annotation;

import com.autumnflix.streaming.core.validation.validator.NumCreditsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumCreditsValidator.class)
@Documented
public @interface NumCreditsConstraint {
    String message() default "Number of credits must be provided and be greater than zero if type is not PREMIUM";
    String premiumMessage() default "Number of credits must be null if type is PREMIUM";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
