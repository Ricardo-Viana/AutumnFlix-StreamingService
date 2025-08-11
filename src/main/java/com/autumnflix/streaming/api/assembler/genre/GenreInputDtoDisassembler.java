package com.autumnflix.streaming.api.assembler.genre;

import com.autumnflix.streaming.api.model.genre.GenreInputDto;
import com.autumnflix.streaming.domain.model.Genre;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GenreInputDtoDisassembler {

    private ModelMapper modelMapper;

    public GenreInputDtoDisassembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Genre toEntityObject(GenreInputDto genreInputDTO) {
        return modelMapper.map(genreInputDTO, Genre.class);
    }

    public void copyToEntityObject(GenreInputDto source, Genre destination) {
        modelMapper.map(source, destination);
    }
}
