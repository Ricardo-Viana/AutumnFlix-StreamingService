package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends CustomJpaRepository<Season, Long> {

    List<Season> findBySeries(Series series);
    Optional<Season> findByNumberAndSeries(Integer seasonNumber, Series series);
}
