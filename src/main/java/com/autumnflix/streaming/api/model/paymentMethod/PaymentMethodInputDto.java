package com.autumnflix.streaming.api.model.paymentMethod;

import com.autumnflix.streaming.core.validation.annotation.CreditCardCVVConstraint;
import com.autumnflix.streaming.core.validation.annotation.CreditCardNumberConstraint;
import com.autumnflix.streaming.domain.model.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@CreditCardCVVConstraint
@CreditCardNumberConstraint
public class PaymentMethodInputDto {

    @NotBlank
    private String securityCode;

    @NotBlank
    private String ownerName;

    @NotBlank
    private String number;

    @NotNull
    private CardType cardType;

    @JsonFormat(pattern = "MM/dd/yyyy")
    @Future(message = "Expiring date must be some date in the future")
    @NotNull
    private LocalDate expiringDate;
}
