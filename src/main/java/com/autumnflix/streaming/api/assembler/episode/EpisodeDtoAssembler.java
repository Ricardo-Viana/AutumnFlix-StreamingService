package com.autumnflix.streaming.api.assembler.episode;

import com.autumnflix.streaming.api.model.episode.EpisodeDto;
import com.autumnflix.streaming.domain.model.Episode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EpisodeDtoAssembler {

    private ModelMapper modelMapper;

    public EpisodeDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EpisodeDto toDto(Episode source) {
        return modelMapper.map(source, EpisodeDto.class);
    }

    public List<EpisodeDto> toCollectionDto(List<Episode> source) {
        return source.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
