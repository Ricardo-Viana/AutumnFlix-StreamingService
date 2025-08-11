package com.autumnflix.streaming.api.assembler.series;

import com.autumnflix.streaming.api.model.series.SeriesDto;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeriesDtoAssembler {

    private ModelMapper modelMapper;

    public SeriesDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SeriesDto toDto(Series source) {
        return modelMapper.map(source, SeriesDto.class);
    }

    public List<SeriesDto> toCollectionDto(List<Series> source) {
        return source.stream()
                .map(item -> toDto(item))
                .toList();
    }
}
