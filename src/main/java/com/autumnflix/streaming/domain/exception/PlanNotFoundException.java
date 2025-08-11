package com.autumnflix.streaming.domain.exception;

public class PlanNotFoundException extends EntityNotFoundException{

    public PlanNotFoundException(String msg){
        super(msg);
    }

    public PlanNotFoundException(Long planId){
        this(String.format("Plan with id %d doesn't exist", planId));
    }
}
