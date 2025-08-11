package com.autumnflix.streaming.api.assembler.paymentMethod;

import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodDto;
import com.autumnflix.streaming.domain.model.PaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentMethodDTOAssembler {

    private ModelMapper modelMapper;

    public PaymentMethodDTOAssembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public PaymentMethodDto toDTO(PaymentMethod paymentMethod){
        return modelMapper.map(paymentMethod, PaymentMethodDto.class);
    }

    public List<PaymentMethodDto> toCollectionDTO(List<PaymentMethod> paymentMethods){
        return paymentMethods.stream().map(
                paymentMethod -> toDTO(paymentMethod)
        ).toList();
    }

}
