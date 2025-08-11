package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.Episode;
import com.autumnflix.streaming.domain.model.Season;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends CustomJpaRepository<Episode, Long> {

    List<Episode> findBySeason(Season season);
    Optional<Episode> getEpisodesBySeasonAndNumber(Season season, Integer number);
}
