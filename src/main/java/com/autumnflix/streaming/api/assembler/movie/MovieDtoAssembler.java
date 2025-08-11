package com.autumnflix.streaming.api.assembler.movie;

import com.autumnflix.streaming.api.model.movie.MovieDto;
import com.autumnflix.streaming.domain.model.Movie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieDtoAssembler {

    private ModelMapper modelMapper;

    public MovieDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MovieDto toDto(Movie source) {
        return modelMapper.map(source, MovieDto.class);
    }

    public List<MovieDto> toCollectionDTO(List<Movie> source) {
        return source.stream()
                .map(this::toDto)
                .toList();
    }
}
