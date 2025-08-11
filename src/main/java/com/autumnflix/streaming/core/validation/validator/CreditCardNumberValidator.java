package com.autumnflix.streaming.core.validation.validator;

import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodInputDto;
import com.autumnflix.streaming.core.validation.annotation.CreditCardNumberConstraint;
import com.autumnflix.streaming.domain.model.CardType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditCardNumberValidator implements ConstraintValidator<CreditCardNumberConstraint, PaymentMethodInputDto> {

    private final int VISA_NUMBER_LENGTH = 16;
    private final int MASTER_CARD_NUMBER_LENGTH = 16;
    private final int AMERICAN_EXPRESS_NUMBER_LENGTH = 15;
    private final int DISCOVER_NUMBER_LENGTH = 16;


    @Override
    public void initialize(CreditCardNumberConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PaymentMethodInputDto paymentMethodInputDTO, ConstraintValidatorContext constraintValidatorContext) {
        if(!isNumberLengthValid(paymentMethodInputDTO)){
            return false;
        }

        if(!luhnAlgorithmValidation(paymentMethodInputDTO)){
            return false;
        }

        return isBINValid(paymentMethodInputDTO);
    }

    private boolean isBINValid(PaymentMethodInputDto paymentMethodInputDTO) {
        String cleanedCardNumber = paymentMethodInputDTO.getNumber().replaceAll("\\s+", ""); // Remove whitespaces

        if (paymentMethodInputDTO.getCardType().equals(CardType.VISA) && cleanedCardNumber.startsWith("4")) {
            return true;
        } else if (paymentMethodInputDTO.getCardType().equals(CardType.MASTERCARD) && cleanedCardNumber.matches("^5[1-5].*")) {
            return true;
        } else if (paymentMethodInputDTO.getCardType().equals(CardType.AMERICANEXPRESS) && (cleanedCardNumber.startsWith("34") || cleanedCardNumber.startsWith("37"))) {
            return true;
        } else if (paymentMethodInputDTO.getCardType().equals(CardType.DISCOVER) &&
                (cleanedCardNumber.startsWith("6011") || cleanedCardNumber.matches("^6(?:22[1-9]|4[4-9]|5[0-9]|4(?:0[1-9]|[1-3][0-9]|9[01]))"))) {
            return true;
        }

        return false;

    }


    private boolean isNumberLengthValid(PaymentMethodInputDto paymentMethodInputDTO){
        if(paymentMethodInputDTO.getCardType().equals(CardType.VISA) && paymentMethodInputDTO.getNumber().length() != VISA_NUMBER_LENGTH ){
            return false;
        } else if (paymentMethodInputDTO.getCardType().equals(CardType.MASTERCARD) && paymentMethodInputDTO.getNumber().length() != MASTER_CARD_NUMBER_LENGTH) {
            return false;
        } else if (paymentMethodInputDTO.getCardType().equals(CardType.AMERICANEXPRESS) && paymentMethodInputDTO.getNumber().length() != AMERICAN_EXPRESS_NUMBER_LENGTH) {
            return false;
        } else if(paymentMethodInputDTO.getCardType().equals(CardType.DISCOVER) && paymentMethodInputDTO.getNumber().length() != DISCOVER_NUMBER_LENGTH){
            return false;
        }

        return true;
    }

    private boolean luhnAlgorithmValidation(PaymentMethodInputDto paymentMethodInputDTO){
        // Luhn algorithm
        int sum = 0;
        boolean alternate = false;

        for (int i = paymentMethodInputDTO.getNumber().length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(paymentMethodInputDTO.getNumber().charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }
}
