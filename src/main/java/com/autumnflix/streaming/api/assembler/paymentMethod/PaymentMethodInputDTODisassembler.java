package com.autumnflix.streaming.api.assembler.paymentMethod;


import com.autumnflix.streaming.api.model.paymentMethod.PaymentMethodInputDto;
import com.autumnflix.streaming.domain.model.PaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodInputDTODisassembler {

    private ModelMapper modelMapper;

    public PaymentMethodInputDTODisassembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public PaymentMethod toEntityObject(PaymentMethodInputDto paymentMethodInputDTO){
        return modelMapper.map(paymentMethodInputDTO, PaymentMethod.class);
    }

    public void copyToEntityObject(PaymentMethodInputDto source, PaymentMethod destination){
        modelMapper.map(source, destination);
    }
}
