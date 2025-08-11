package com.autumnflix.streaming.core.validation.annotation;

import com.autumnflix.streaming.core.validation.validator.ReleaseYearValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = ReleaseYearValidator.class
)
public @interface ReleaseYearConstraint {

    String message() default "The release year of the movie must be on or after 1895.";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
