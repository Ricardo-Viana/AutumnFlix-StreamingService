package com.autumnflix.streaming.api.assembler.season;

import com.autumnflix.streaming.api.model.season.SeasonInputDto;
import com.autumnflix.streaming.domain.model.Season;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SeasonInputDtoAssembler {

    private ModelMapper modelMapper;

    public SeasonInputDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SeasonInputDto toInputDto(Season source) {
        return modelMapper.map(source, SeasonInputDto.class);
    }
}
