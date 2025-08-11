package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.genre.GenreDtoAssembler;
import com.autumnflix.streaming.api.assembler.genre.GenreInputDtoDisassembler;
import com.autumnflix.streaming.api.model.genre.GenreDto;
import com.autumnflix.streaming.api.model.genre.GenreInputDto;
import com.autumnflix.streaming.domain.model.Genre;
import com.autumnflix.streaming.domain.service.GenreService;
import com.autumnflix.streaming.domain.service.GenreServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private GenreService genreService;
    private GenreDtoAssembler genreDTOAssembler;
    private GenreInputDtoDisassembler genreInputDTODisassembler;

    public GenreController(GenreServiceImpl genreService, GenreDtoAssembler genreDTOAssembler,
                           GenreInputDtoDisassembler genreInputDTODisassembler) {
        this.genreService = genreService;
        this.genreDTOAssembler = genreDTOAssembler;
        this.genreInputDTODisassembler = genreInputDTODisassembler;
    }

    @GetMapping
    public List<GenreDto> getAll() {
        return genreDTOAssembler
                .toCollectionDto(genreService.getAll());
    }

    @GetMapping("/{genreId}")
    public GenreDto getById(@PathVariable("genreId") Long genreId) {
        return genreDTOAssembler
                .toDto(genreService.getGenre(genreId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDto add(@RequestBody @Valid GenreInputDto genreInputDTO) {
        Genre genre = genreInputDTODisassembler.toEntityObject(genreInputDTO);

        return genreDTOAssembler.toDto(genreService.insert(genre));
    }

    @PutMapping("/{genreId}")
    public GenreDto update(@PathVariable("genreId") Long genreId, @RequestBody @Valid GenreInputDto genreInputDTO) {
        Genre existingGenre = genreService.getGenre(genreId);

        genreInputDTODisassembler.copyToEntityObject(genreInputDTO, existingGenre);

        return genreDTOAssembler.toDto(genreService.insert(existingGenre));
    }

    @DeleteMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long genreId) {
        genreService.delete(genreId);
    }
}
