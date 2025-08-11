package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.SeriesNotFoundException;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.repository.SeriesRepository;
import com.autumnflix.streaming.infraestructure.repository.specs.SeriesSpecs;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeriesServiceIMPL implements SeriesService {

    private SeriesRepository seriesRepository;
    private EntertainmentWorkService entertainmentWorkService;
    private GenreService genreService;

    public SeriesServiceIMPL(SeriesRepository seriesRepository, EntertainmentWorkService entertainmentWorkService, GenreService genreService) {
        this.seriesRepository = seriesRepository;
        this.entertainmentWorkService = entertainmentWorkService;
        this.genreService = genreService;
    }

    @Override
    public List<Series> getAll(EntertainmentWorkTypelessFilter filter) {
        return seriesRepository.findAll(SeriesSpecs.usingFilter(filter));
    }

    @Override
    public Series getSeries(Long seriesId) {
        return seriesRepository.findById(seriesId)
                .orElseThrow(() -> new SeriesNotFoundException(seriesId));
    }

    @Override
    @Transactional
    public Series insert(Series series) {
        series.setType(EntertainmentWorkType.SERIES);

        return seriesRepository.save(series);
    }

    @Override
    @Transactional
    public void delete(Long seriesId) {
        Series series = getSeries(seriesId);

        try {
            seriesRepository.delete(series);
            seriesRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityBeingUsedException("Series", seriesId);
        }
    }

    @Override
    @Transactional
    public void associateGenre(Long seriesId, Long genreId) {
        Series series = getSeries(seriesId);
        Genre genre = genreService.getGenre(genreId);

        series.addGenre(genre);
    }

    @Override
    @Transactional
    public void disassociateGenre(Long seriesId, Long genreId) {
        Series series = getSeries(seriesId);
        Genre genre = genreService.getGenre(genreId);

        series.removeGenre(genre);
    }
}
