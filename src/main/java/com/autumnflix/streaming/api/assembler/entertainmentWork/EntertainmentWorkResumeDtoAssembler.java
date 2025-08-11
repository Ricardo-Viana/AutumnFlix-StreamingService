package com.autumnflix.streaming.api.assembler.entertainmentWork;

import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkResumeDto;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntertainmentWorkResumeDtoAssembler {

    private ModelMapper modelMapper;

    public EntertainmentWorkResumeDtoAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EntertainmentWorkResumeDto toDto(EntertainmentWork source) {
        return modelMapper.map(source, EntertainmentWorkResumeDto.class);
    }

    public List<EntertainmentWorkResumeDto> toCollectionDto(List<EntertainmentWork> source) {
        return source.stream()
                .map(this::toDto)
                .toList();
    }
}
