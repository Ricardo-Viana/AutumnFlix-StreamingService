package com.autumnflix.streaming.core.validation.annotation;

import com.autumnflix.streaming.core.validation.validator.MinimumAgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumAgeValidator.class)
@Documented
public @interface MinimumAgeConstraint {

    String message() default "The minimum age to create an account is 18 years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
