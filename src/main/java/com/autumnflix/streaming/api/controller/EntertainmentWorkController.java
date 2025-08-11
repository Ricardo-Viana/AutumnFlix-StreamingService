package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.entertainmentWork.EntertainmentWorkDtoAssembler;
import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkDto;
import com.autumnflix.streaming.api.assembler.entertainmentWork.EntertainmentWorkResumeDtoAssembler;
import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkResumeDto;
import com.autumnflix.streaming.domain.filter.EntertainmentWorkFilter;
import com.autumnflix.streaming.domain.model.EntertainmentWorkType;
import com.autumnflix.streaming.domain.service.EntertainmentWorkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/entertainment-works")
public class EntertainmentWorkController {

    private EntertainmentWorkService entertainmentWorkService;
    private EntertainmentWorkDtoAssembler entertainmentWorkDTOAssembler;
    private EntertainmentWorkResumeDtoAssembler entertainmentWorkResumeDtoAssembler;

    public EntertainmentWorkController(EntertainmentWorkService entertainmentWorkService,
                                       EntertainmentWorkDtoAssembler entertainmentWorkDTOAssembler,
                                       EntertainmentWorkResumeDtoAssembler entertainmentWorkResumeDtoAssembler) {
        this.entertainmentWorkService = entertainmentWorkService;
        this.entertainmentWorkDTOAssembler = entertainmentWorkDTOAssembler;
        this.entertainmentWorkResumeDtoAssembler = entertainmentWorkResumeDtoAssembler;
    }

    @GetMapping
    public List<EntertainmentWorkResumeDto> getAll(EntertainmentWorkFilter filter) {
        return entertainmentWorkResumeDtoAssembler.toCollectionDto(entertainmentWorkService.getAll(filter));
    }

    @GetMapping("/{entertainmentWorkId}")
    public EntertainmentWorkDto getById(@PathVariable("entertainmentWorkId") Long entertainmentWorkId) {
        return entertainmentWorkDTOAssembler.toDto(entertainmentWorkService.getEntertainmentWork(entertainmentWorkId));
    }
}
