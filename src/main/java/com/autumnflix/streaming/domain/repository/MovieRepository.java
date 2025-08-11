package com.autumnflix.streaming.domain.repository;

import com.autumnflix.streaming.domain.model.Movie;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends CustomJpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
}
