package com.autumnflix.streaming.api.assembler.season;

import com.autumnflix.streaming.api.model.season.SeasonDto;
import com.autumnflix.streaming.domain.model.Season;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeasonDtoAssembler {

    private ModelMapper modelMapper;

    public SeasonDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public SeasonDto toDto(Season source) {
        return modelMapper.map(source, SeasonDto.class);
    }

    public List<SeasonDto> toCollectionDto(Collection<Season> source) {
        return source.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
