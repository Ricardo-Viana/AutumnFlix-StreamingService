package com.autumnflix.streaming.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class PaymentMethod {

    @Column(name="payment_method_security_code", nullable = false)
    private String securityCode;

    @Column(name = "payment_method_owner_name", nullable = false)
    private String ownerName;

    @Column(name = "payment_method_number", nullable = false)
    private String number;

    @Column(name = "payment_method_card_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "payment_method_expiring_date", nullable = false)
    private LocalDate expiringDate;

}
