package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.plan.PlanDTOAssembler;
import com.autumnflix.streaming.api.assembler.plan.PlanInputDTODisassembler;
import com.autumnflix.streaming.api.model.plan.PlanDTO;
import com.autumnflix.streaming.api.model.plan.PlanInputDTO;
import com.autumnflix.streaming.domain.model.Plan;
import com.autumnflix.streaming.domain.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private PlanService planService;
    private PlanDTOAssembler planDTOAssembler;
    private PlanInputDTODisassembler planInputDTODisassembler;

    public PlanController(PlanService planService,
                          PlanDTOAssembler planDTOAssembler,
                          PlanInputDTODisassembler planInputDTODisassembler) {
        this.planService = planService;
        this.planDTOAssembler = planDTOAssembler;
        this.planInputDTODisassembler = planInputDTODisassembler;
    }

    @GetMapping
    public List<PlanDTO> getAll(){
        return planDTOAssembler
                .toCollectionDTO(planService.getAll());
    }

    @GetMapping("/{planId}")
    public PlanDTO getById(@PathVariable("planId") Long planId){
        return planDTOAssembler
                .toDTO(planService.getPlan(planId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanDTO add(@RequestBody @Valid PlanInputDTO planInputDTO){
        Plan plan = planInputDTODisassembler.toEntityObject(planInputDTO);

        return planDTOAssembler.toDTO(planService.insert(plan));
    }

    @PutMapping("/{planId}")
    @ResponseStatus(HttpStatus.OK)
    public PlanDTO update(@PathVariable("planId") Long planId, @RequestBody @Valid PlanInputDTO planInputDTO){
        Plan existingPlan = planService.getPlan(planId);

        planInputDTODisassembler.copyToEntityObject(planInputDTO, existingPlan);

        return planDTOAssembler.toDTO(planService.insert(existingPlan));
    }

    @DeleteMapping("/{planId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("planId") Long planId){
        planService.delete(planId);
    }
}
