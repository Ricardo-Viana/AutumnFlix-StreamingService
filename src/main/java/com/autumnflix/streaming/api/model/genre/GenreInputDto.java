package com.autumnflix.streaming.api.model.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreInputDto {

    @NotBlank
    private String name;
}
