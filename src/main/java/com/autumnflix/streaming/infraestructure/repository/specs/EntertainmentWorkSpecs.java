package com.autumnflix.streaming.infraestructure.repository.specs;

import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.Genre;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EntertainmentWorkSpecs {

    public static Specification<EntertainmentWork> usingFilter(EntertainmentWorkFilter filter) {
        return ((root, query, builder) -> {

            var predicates = buildPredicate(root, builder, filter);

            if (filter.getType() != null) {
                predicates.add(builder.equal(root.get("type"), filter.getType()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static <T extends EntertainmentWorkTypelessFilter> List<Predicate> buildPredicate(
            Root<? extends EntertainmentWork> root, CriteriaBuilder builder, T filter) {

        List<Predicate> predicates = new ArrayList<>();

        if (filter.getName() != null) {
            predicates.add(builder.like(root.get("name"), "%" + filter.getName() + "%"));
        }

        if (filter.getRelevance() != null) {
            predicates.add(builder.equal(root.get("relevance"), filter.getRelevance()));
        }

        if (filter.getRating() != null) {
            predicates.add(builder.equal(root.get("parentalRating"), filter.getRating()));
        }

        if (filter.getInitialReleaseYear() != null) {
            predicates.add(builder.greaterThanOrEqualTo(
                    root.get("releaseYear"), filter.getInitialReleaseYear()));
        }

        if (filter.getFinalReleaseYear() != null) {
            predicates.add(builder.lessThanOrEqualTo(
                    root.get("releaseYear"), filter.getFinalReleaseYear()));
        }

        if (filter.getGenreId() != null) {
            Join<EntertainmentWork, Genre> genreJoin = root.join("genres", JoinType.INNER);
            predicates.add(builder.equal(
                    genreJoin.get("id"), filter.getGenreId()));
        }

        return predicates;
    }
}
