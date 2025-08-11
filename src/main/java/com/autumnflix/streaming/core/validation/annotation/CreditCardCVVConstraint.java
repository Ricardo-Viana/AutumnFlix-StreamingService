package com.autumnflix.streaming.core.validation.annotation;

import com.autumnflix.streaming.core.validation.validator.CreditCardCVVValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditCardCVVValidator.class)
@Documented
public @interface CreditCardCVVConstraint {
    String message() default "Credit card cvv must be 3 digits for Visa,MasterCard and Discover " +
            "or 4 digits for American Express";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
