package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeriesRepository extends CustomJpaRepository<Series, Long>, JpaSpecificationExecutor<Series> {

    @Query("from Series s left join fetch s.seasons")
    List<Series> findAll(EntertainmentWorkTypelessFilter filter);

    @Query("from Series s left join fetch s.seasons left join fetch s.genres where s.id = :id")
    Optional<Series> findById(Long id);
}
