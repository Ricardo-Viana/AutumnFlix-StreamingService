package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.PlanNotFoundException;
import com.autumnflix.streaming.domain.model.Plan;
import com.autumnflix.streaming.domain.model.PlanType;
import com.autumnflix.streaming.domain.repository.PlanRepository;
import com.autumnflix.streaming.domain.repository.UserRepository;
import com.autumnflix.streaming.domain.service.PlanService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanServiceIMPL implements PlanService {

    private PlanRepository planRepository;

    public PlanServiceIMPL(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
    @Override
    public List<Plan> getAll() {
        return planRepository.findAll();
    }

    @Override
    public Plan getPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(planId));
    }

    @Override
    @Transactional
    public Plan insert(Plan plan) {
        return planRepository.save(plan);
    }

    @Override
    @Transactional
    public void delete(Long planId) {
        Plan plan = getPlan(planId);

        try {
            planRepository.delete(plan);
        }catch (DataIntegrityViolationException e){
            throw new EntityBeingUsedException("Plan", planId);
        }
    }
}
