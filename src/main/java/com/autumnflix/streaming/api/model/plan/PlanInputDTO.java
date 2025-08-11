package com.autumnflix.streaming.api.model.plan;

import com.autumnflix.streaming.core.validation.annotation.NumCreditsConstraint;
import com.autumnflix.streaming.domain.model.PlanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NumCreditsConstraint(message = "Number of credits must be provided and be greater than zero if type is not PREMIUM",
        premiumMessage = "Number of credits must be null if type is PREMIUM")
public class PlanInputDTO {

    @NotNull
    private PlanType type;

    private Integer numCredits;

    @Positive
    @NotNull
    private BigDecimal value;

    private String description;
}
