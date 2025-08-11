package com.autumnflix.streaming.api.assembler.movie;

import com.autumnflix.streaming.api.model.movie.MovieInputDto;
import com.autumnflix.streaming.domain.model.Movie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MovieInputDtoAssembler {

    private ModelMapper modelMapper;

    public MovieInputDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MovieInputDto toInputDto(Movie movie) {
        return modelMapper.map(movie, MovieInputDto.class);
    }
}
