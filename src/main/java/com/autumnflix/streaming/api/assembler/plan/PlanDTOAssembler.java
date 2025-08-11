package com.autumnflix.streaming.api.assembler.plan;

import com.autumnflix.streaming.api.model.plan.PlanDTO;
import com.autumnflix.streaming.domain.model.Plan;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanDTOAssembler {

    private ModelMapper modelMapper;

    public PlanDTOAssembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public PlanDTO toDTO(Plan plan){
        return modelMapper.map(plan, PlanDTO.class);
    }

    public List<PlanDTO> toCollectionDTO(List<Plan> plans){
        return plans.stream()
                .map(this::toDTO)
                .toList();
    }
}
