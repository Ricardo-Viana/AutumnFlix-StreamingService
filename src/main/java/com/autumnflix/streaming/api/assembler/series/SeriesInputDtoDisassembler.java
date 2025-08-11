package com.autumnflix.streaming.api.assembler.series;

import com.autumnflix.streaming.api.model.series.SeriesInputDto;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SeriesInputDtoDisassembler {

    private ModelMapper modelMapper;

    public SeriesInputDtoDisassembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Series toEntityObject(SeriesInputDto seriesInputDto) {
        return modelMapper.map(seriesInputDto, Series.class);
    }

    public void copyToEntity(SeriesInputDto source, Series destination) {
        modelMapper.map(source, destination);
    }
}
