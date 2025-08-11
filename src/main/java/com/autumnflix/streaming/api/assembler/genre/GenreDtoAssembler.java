package com.autumnflix.streaming.api.assembler.genre;

import com.autumnflix.streaming.api.model.genre.GenreDto;
import com.autumnflix.streaming.domain.model.Genre;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GenreDtoAssembler {

    private ModelMapper modelMapper;

    public GenreDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public GenreDto toDto(Genre source) {
        return modelMapper.map(source, GenreDto.class);
    }


    public List<GenreDto> toCollectionDto(Collection<Genre> source) {
        return source.stream()
                .map(this::toDto)
                .toList();
    }
}
