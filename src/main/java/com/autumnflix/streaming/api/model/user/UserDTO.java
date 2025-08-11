package com.autumnflix.streaming.api.model.user;

import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodDto;
import com.autumnflix.streaming.api.model.plan.PlanDTO;
import com.autumnflix.streaming.api.model.identificationDocument.IdentificationDocumentDTO;
import com.autumnflix.streaming.domain.model.Credit;
import com.autumnflix.streaming.domain.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserDTO {

    private Long id;

    private String fullName;

    private String email;

    private String password;

    private LocalDate dob;

    private PlanDTO plan;

    private IdentificationDocumentDTO identificationDocument;

    private PaymentMethodDto paymentMethod;

    private Credit credit;

    private Role role;
}
