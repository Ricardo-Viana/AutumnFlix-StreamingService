package com.autumnflix.streaming.core.validation.annotation;


import com.autumnflix.streaming.core.validation.validator.CreditCardNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditCardNumberValidator.class)
@Documented
public @interface CreditCardNumberConstraint {
    String message() default "Credit card number must be valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
