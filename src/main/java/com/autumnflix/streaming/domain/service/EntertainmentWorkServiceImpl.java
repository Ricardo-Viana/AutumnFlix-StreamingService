package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.EntertainmentWorkNotFoundException;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import com.autumnflix.streaming.domain.repository.EntertainmentWorkRepository;
import com.autumnflix.streaming.infraestructure.repository.specs.EntertainmentWorkSpecs;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntertainmentWorkServiceImpl implements EntertainmentWorkService{

    private EntertainmentWorkRepository entertainmentWorkRepository;

    public EntertainmentWorkServiceImpl(EntertainmentWorkRepository entertainmentWorkRepository) {
        this.entertainmentWorkRepository = entertainmentWorkRepository;
    }

    @Override
    public List<EntertainmentWork> getAll(EntertainmentWorkFilter filter) {
        return entertainmentWorkRepository.findAll(EntertainmentWorkSpecs.usingFilter(filter));
    }

    @Override
    public EntertainmentWork getEntertainmentWork(Long entertainmentWorkId) {
        return entertainmentWorkRepository.findById(entertainmentWorkId)
                .orElseThrow(() -> new EntertainmentWorkNotFoundException(entertainmentWorkId));
    }

    @Override
    @Transactional
    public EntertainmentWork insert(EntertainmentWork entertainmentWork) {
        return entertainmentWorkRepository.save(entertainmentWork);
    }

    @Override
    @Transactional
    public void delete(Long entertainmentWorkId) {
        EntertainmentWork entertainmentWork = getEntertainmentWork(entertainmentWorkId);

        entertainmentWorkRepository.delete(entertainmentWork);
    }
}
