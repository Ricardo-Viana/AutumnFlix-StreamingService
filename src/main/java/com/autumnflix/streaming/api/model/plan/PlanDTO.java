package com.autumnflix.streaming.api.model.plan;

import com.autumnflix.streaming.domain.model.PlanType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PlanDTO {

    private Long id;
    private PlanType type;
    private Integer numCredits;
    private BigDecimal value;
    private String description;
}
