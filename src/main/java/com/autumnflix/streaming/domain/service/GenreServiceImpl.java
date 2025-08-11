package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.GenreNotFoundException;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.repository.GenreRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> getAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre getGenre(Long genreId) {
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreNotFoundException(genreId));
    }

    @Override
    @Transactional
    public Genre insert(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public void delete(Long genreId) {
        Genre genre = getGenre(genreId);

        try {
            genreRepository.delete(genre);
            genreRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityBeingUsedException("Genre", genreId);
        }
    }
}
