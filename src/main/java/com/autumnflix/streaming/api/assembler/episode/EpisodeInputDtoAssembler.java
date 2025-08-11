package com.autumnflix.streaming.api.assembler.episode;

import com.autumnflix.streaming.api.model.episode.EpisodeInputDto;
import com.autumnflix.streaming.domain.model.Episode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EpisodeInputDtoAssembler {

    private ModelMapper modelMapper;

    public EpisodeInputDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EpisodeInputDto toInputDto(Episode source) {
        return modelMapper.map(source, EpisodeInputDto.class);
    }
}
