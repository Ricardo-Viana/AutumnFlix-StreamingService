package com.autumnflix.streaming.api.assembler.movie;

import com.autumnflix.streaming.api.model.movie.MovieInputDto;
import com.autumnflix.streaming.domain.model.Movie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MovieDtoDisassembler {

    private ModelMapper modelMapper;

    public MovieDtoDisassembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Movie toEntityObject(MovieInputDto source) {
        return modelMapper.map(source, Movie.class);
    }

    public void copyToEntityObject(MovieInputDto source, Movie destination) {
        modelMapper.map(source, destination);
    }
}
