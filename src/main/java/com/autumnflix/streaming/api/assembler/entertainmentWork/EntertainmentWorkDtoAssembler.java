package com.autumnflix.streaming.api.assembler.entertainmentWork;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkDto;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class EntertainmentWorkDtoAssembler {

    private ModelMapper modelMapper;

    public EntertainmentWorkDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public EntertainmentWorkDto toDto(EntertainmentWork source) {
        return modelMapper.map(source, EntertainmentWorkDto.class);
    }

    public List<EntertainmentWorkDto> toCollectionDto(Collection<EntertainmentWork> source) {
        return source.stream()
                .map(this::toDto)
                .toList();
    }
}
