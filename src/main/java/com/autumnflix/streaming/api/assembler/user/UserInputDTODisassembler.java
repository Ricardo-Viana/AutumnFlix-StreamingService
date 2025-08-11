package com.autumnflix.streaming.api.assembler.user;

import com.autumnflix.streaming.api.model.user.UserInputDTO;
import com.autumnflix.streaming.domain.model.Plan;
import com.autumnflix.streaming.domain.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserInputDTODisassembler {

    private ModelMapper modelMapper;

    public UserInputDTODisassembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public User toEntityObject(UserInputDTO userInputDTO){
        return modelMapper.map(userInputDTO, User.class);
    }

    public void copyToEntityObject(UserInputDTO source, User destination){
        destination.setPlan(new Plan());

        modelMapper.map(source, destination);
    }
}
