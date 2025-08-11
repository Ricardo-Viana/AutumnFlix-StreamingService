package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.model.Plan;

import java.util.List;

public interface PlanService {

    List<Plan> getAll();

    Plan getPlan(Long planId);

    Plan insert(Plan plan);

    void delete(Long planId);
}
