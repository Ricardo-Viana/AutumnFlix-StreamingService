package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.EntertainmentWorkType;

import java.util.List;

public interface EntertainmentWorkService {

    List<EntertainmentWork> getAll(EntertainmentWorkFilter filter);
    EntertainmentWork getEntertainmentWork(Long entertainmentWorkId);
    EntertainmentWork insert(EntertainmentWork entertainmentWork);
    void delete(Long entertainmentWorkId);
}
