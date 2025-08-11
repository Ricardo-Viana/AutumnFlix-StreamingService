package com.autumnflix.streaming.api.assembler.movie;

import com.autumnflix.streaming.api.model.movie.MovieResumeDto;
import com.autumnflix.streaming.domain.model.Movie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieResumeDtoAssembler {

    private ModelMapper modelMapper;

    public MovieResumeDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MovieResumeDto toDto(Movie source) {
        return modelMapper.map(source, MovieResumeDto.class);
    }

    public List<MovieResumeDto> toCollectionDTO(List<Movie> source) {
        return source.stream()
                .map(this::toDto)
                .toList();
    }
}
