package com.autumnflix.streaming.core.validation.validator;


import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodInputDto;
import com.autumnflix.streaming.core.validation.annotation.CreditCardCVVConstraint;
import com.autumnflix.streaming.domain.model.CardType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditCardCVVValidator implements ConstraintValidator<CreditCardCVVConstraint, PaymentMethodInputDto> {


    @Override
    public void initialize(CreditCardCVVConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PaymentMethodInputDto paymentMethodInputDTO, ConstraintValidatorContext constraintValidatorContext) {
        if(paymentMethodInputDTO.getCardType() == CardType.AMERICANEXPRESS){
            return paymentMethodInputDTO.getSecurityCode().matches("\\d{4}");
        }
        else{
            return paymentMethodInputDTO.getSecurityCode().matches("\\d{3}");
        }
    }
}
