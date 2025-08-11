package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntertainmentWorkRepository extends CustomJpaRepository<EntertainmentWork, Long>,
        JpaSpecificationExecutor<EntertainmentWork> {

    List<EntertainmentWork> findAllByType(EntertainmentWorkType type);
}
