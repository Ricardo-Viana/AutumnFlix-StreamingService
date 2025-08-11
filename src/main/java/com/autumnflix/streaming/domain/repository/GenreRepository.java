package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.Genre;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends CustomJpaRepository<Genre, Long> {
}
