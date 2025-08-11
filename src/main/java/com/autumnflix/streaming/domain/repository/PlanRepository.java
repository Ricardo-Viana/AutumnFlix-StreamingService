package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends CustomJpaRepository<Plan, Long> {
}
