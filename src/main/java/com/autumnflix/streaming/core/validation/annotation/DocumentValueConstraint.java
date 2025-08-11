package com.autumnflix.streaming.core.validation.annotation;

import com.autumnflix.streaming.core.validation.validator.DocumentValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DocumentValueValidator.class)
@Documented
public @interface DocumentValueConstraint {
    String message() default "Document value must be valid for the type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
