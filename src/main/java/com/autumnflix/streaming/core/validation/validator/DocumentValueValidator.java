package com.autumnflix.streaming.core.validation.validator;

import com.autumnflix.streaming.api.model.identificationDocument.IdentificationDocumentDTO;
import com.autumnflix.streaming.core.validation.annotation.DocumentValueConstraint;
import com.autumnflix.streaming.domain.model.IdentificationDocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class DocumentValueValidator implements ConstraintValidator<DocumentValueConstraint, IdentificationDocumentDTO> {

    private String ssnPattern = "^(?!000|666|9\\d\\d)\\d{3}-(?!00)\\d{2}-(?!0000)\\d{4}$";

    private String einPattern = "^\\d{2}-\\d{7}$";

    @Override
    public boolean isValid(IdentificationDocumentDTO identificationDocumentDTO, ConstraintValidatorContext constraintValidatorContext) {
        if(identificationDocumentDTO.getType() == IdentificationDocumentType.SSN){
            return Pattern.compile(ssnPattern).matcher(identificationDocumentDTO.getValue()).matches();
        }
        else{
            return Pattern.compile(einPattern).matcher(identificationDocumentDTO.getValue()).matches();
        }
    }
}
