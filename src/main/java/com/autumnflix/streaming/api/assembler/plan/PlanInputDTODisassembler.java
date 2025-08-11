package com.autumnflix.streaming.api.assembler.plan;

import com.autumnflix.streaming.api.model.plan.PlanInputDTO;
import com.autumnflix.streaming.domain.model.Plan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PlanInputDTODisassembler {

    private ModelMapper modelMapper;

    public PlanInputDTODisassembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public Plan toEntityObject(PlanInputDTO planInputDTO){
        return modelMapper.map(planInputDTO, Plan.class);
    }

    public void copyToEntityObject(PlanInputDTO source, Plan destination){
        modelMapper.map(source, destination);
    }

}
