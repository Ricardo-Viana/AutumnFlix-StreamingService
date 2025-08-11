package com.autumnflix.streaming.api.assembler.series;

import com.autumnflix.streaming.api.model.series.SeriesInputDto;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SeriesInputDtoAssembler {

    private ModelMapper modelMapper;

    public SeriesInputDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SeriesInputDto toInputDto(Series source) {
        return modelMapper.map(source, SeriesInputDto.class);
    }
}
