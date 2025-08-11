package com.autumnflix.streaming.api.model.identificationDocument;

import com.autumnflix.streaming.core.validation.annotation.DocumentValueConstraint;
import com.autumnflix.streaming.domain.model.IdentificationDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DocumentValueConstraint
public class IdentificationDocumentDTO {

    @NotNull
    private IdentificationDocumentType type;

    @NotBlank
    private String value;
}
