package com.autumnflix.streaming.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class IdentificationDocument {

    @Column(name = "identification_document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentificationDocumentType type;

    @Column(name = "identification_document_value", unique = true, nullable = false)
    private String value;
}
