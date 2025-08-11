package com.autumnflix.streaming.api.model.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPlanInputDTO {

    @NotNull
    private Long id;
}
