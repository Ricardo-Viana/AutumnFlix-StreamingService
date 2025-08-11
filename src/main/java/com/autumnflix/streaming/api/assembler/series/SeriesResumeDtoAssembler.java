package com.autumnflix.streaming.api.assembler.series;

import com.autumnflix.streaming.api.model.series.SeriesDto;
import com.autumnflix.streaming.api.model.series.SeriesResumeDto;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeriesResumeDtoAssembler {

    private ModelMapper modelMapper;

    public SeriesResumeDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SeriesResumeDto toDto(Series source) {
        return modelMapper.map(source, SeriesResumeDto.class);
    }

    public List<SeriesResumeDto> toCollectionDto(List<Series> source) {
        return source.stream()
                .map(item -> toDto(item))
                .toList();
    }
}
