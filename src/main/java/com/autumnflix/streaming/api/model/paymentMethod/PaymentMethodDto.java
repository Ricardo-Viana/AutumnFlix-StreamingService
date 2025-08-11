package com.autumnflix.streaming.api.model.paymentMethod;

import com.autumnflix.streaming.domain.model.CardType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentMethodDto {

    private String ownerName;

    private CardType cardType;

    private LocalDate expiringDate;
}
