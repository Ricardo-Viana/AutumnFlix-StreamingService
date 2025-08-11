package com.autumnflix.streaming.api.assembler.season;

import com.autumnflix.streaming.api.model.season.SeasonInputDto;
import com.autumnflix.streaming.domain.model.Season;
import com.autumnflix.streaming.domain.model.Series;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SeasonDtoDisassembler {

    private ModelMapper modelMapper;

    public SeasonDtoDisassembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Season toEntityObject(SeasonInputDto seasonInputDto) {
        return modelMapper.map(seasonInputDto, Season.class);
    }

    public void copyToEntityObject(SeasonInputDto source, Season destination) {
        modelMapper.map(source, destination);
    }
}
