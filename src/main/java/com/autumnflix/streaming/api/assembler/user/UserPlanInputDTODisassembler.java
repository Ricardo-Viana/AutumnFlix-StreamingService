package com.autumnflix.streaming.api.assembler.user;

import com.autumnflix.streaming.api.model.user.UserPlanInputDTO;
import com.autumnflix.streaming.domain.model.Plan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserPlanInputDTODisassembler {

    private ModelMapper modelMapper;

    public UserPlanInputDTODisassembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public Plan toEntityObject(UserPlanInputDTO userPlanInputDTO){
        return modelMapper.map(userPlanInputDTO,Plan.class);
    }

    public void copyToEntityObject(UserPlanInputDTO source, Plan destination){
        modelMapper.map(source, destination);
    }
}
