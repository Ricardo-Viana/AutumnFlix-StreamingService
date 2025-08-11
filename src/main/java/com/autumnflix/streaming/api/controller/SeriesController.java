package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.series.SeriesDtoAssembler;
import com.autumnflix.streaming.api.assembler.series.SeriesInputDtoDisassembler;
import com.autumnflix.streaming.api.assembler.series.SeriesResumeDtoAssembler;
import com.autumnflix.streaming.api.model.series.SeriesDto;
import com.autumnflix.streaming.api.model.series.SeriesInputDto;
import com.autumnflix.streaming.api.model.series.SeriesResumeDto;
import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.GenreNotFoundException;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkTypelessFilter;
import com.autumnflix.streaming.domain.model.Series;
import com.autumnflix.streaming.domain.service.SeriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {

    private SeriesDtoAssembler seriesDTOAssembler;
    private SeriesResumeDtoAssembler seriesResumeDtoAssembler;
    private SeriesInputDtoDisassembler seriesInputDTODisassembler;
    private SeriesService seriesService;

    public SeriesController(SeriesDtoAssembler seriesDTOAssembler, SeriesInputDtoDisassembler seriesInputDTODisassembler,
                            SeriesService seriesService, SeriesResumeDtoAssembler seriesResumeDtoAssembler) {
        this.seriesDTOAssembler = seriesDTOAssembler;
        this.seriesInputDTODisassembler = seriesInputDTODisassembler;
        this.seriesService = seriesService;
        this.seriesResumeDtoAssembler = seriesResumeDtoAssembler;
    }

    @GetMapping
    public List<SeriesResumeDto> getAll(EntertainmentWorkTypelessFilter filter) {
        return seriesResumeDtoAssembler.toCollectionDto(seriesService.getAll(filter));
    }

    @GetMapping("/{seriesId}")
    public SeriesDto getById(@PathVariable("seriesId") Long seriesId) {
        return seriesDTOAssembler.toDto(seriesService.getSeries(seriesId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeriesDto add(@RequestBody @Valid SeriesInputDto seriesInputDto) {
        try {
            Series series = seriesInputDTODisassembler.toEntityObject(seriesInputDto);

            return seriesDTOAssembler.toDto(seriesService.insert(series));
        } catch (GenreNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{seriesId}")
    public SeriesDto update(@PathVariable("seriesId") Long seriesId, @Valid @RequestBody SeriesInputDto source) {
        try {
            Series existingSeries = seriesService.getSeries(seriesId);

            seriesInputDTODisassembler.copyToEntity(source, existingSeries);

            return seriesDTOAssembler.toDto(seriesService.insert(existingSeries));
        } catch (GenreNotFoundException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @DeleteMapping("/{seriesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("seriesId") Long seriesId) {
        seriesService.delete(seriesId);
    }
}
