package com.autumnflix.streaming.infraestructure.repository.specs;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Movie;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecs {

    public static Specification<Movie> usingFilter(EntertainmentWorkTypelessFilter filter) {
        return ((root, query, builder) -> {
            var predicates = EntertainmentWorkSpecs.buildPredicate(root, builder, filter);

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
