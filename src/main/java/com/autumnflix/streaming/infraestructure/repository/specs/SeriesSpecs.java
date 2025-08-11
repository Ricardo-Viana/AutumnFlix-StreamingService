package com.autumnflix.streaming.infraestructure.repository.specs;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Movie;
import com.autumnflix.streaming.domain.model.Series;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SeriesSpecs {

    public static Specification<Series> usingFilter(EntertainmentWorkTypelessFilter filter) {
        return ((root, query, builder) -> {
            var predicates = EntertainmentWorkSpecs.buildPredicate(root, builder, filter);

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
