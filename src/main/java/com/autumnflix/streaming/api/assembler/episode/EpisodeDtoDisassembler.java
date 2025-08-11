package com.autumnflix.streaming.api.assembler.episode;

import com.autumnflix.streaming.api.model.episode.EpisodeDto;
import com.autumnflix.streaming.api.model.episode.EpisodeInputDto;
import com.autumnflix.streaming.domain.model.Episode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EpisodeDtoDisassembler {

    private ModelMapper modelMapper;

    public EpisodeDtoDisassembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Episode toEntityObject(EpisodeInputDto source) {
        return modelMapper.map(source, Episode.class);
    }

    public void copyToEntityObject(EpisodeInputDto source, Episode destination) {
        modelMapper.map(source, destination);
    }
}
