package com.autumnflix.streaming.api.model.user;

import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodInputDto;
import com.autumnflix.streaming.api.model.identificationDocument.IdentificationDocumentDTO;
import com.autumnflix.streaming.core.validation.annotation.MinimumAgeConstraint;
import com.autumnflix.streaming.domain.model.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserInputDTO {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @JsonFormat(pattern = "MM/dd/yyyy")
    @MinimumAgeConstraint
    @NotNull
    private LocalDate dob;

    @NotBlank
    private String password;

    @Valid
    private UserPlanInputDTO plan;

    @Valid
    private PaymentMethodInputDto paymentMethod;

    @Valid
    private IdentificationDocumentDTO identificationDocument;

    @NotNull
    private Role role;
}
