package com.autumnflix.streaming.api.assembler.identificationDocument;

import com.autumnflix.streaming.api.model.identificationDocument.IdentificationDocumentDTO;
import com.autumnflix.streaming.domain.model.IdentificationDocument;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IdentificationDocumentDTOAssembler {

    private ModelMapper modelMapper;

    public IdentificationDocumentDTOAssembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public IdentificationDocumentDTO toDTO(IdentificationDocument identificationDocument){
        return modelMapper.map(identificationDocument, IdentificationDocumentDTO.class);
    }

    public List<IdentificationDocumentDTO> toCollectionDTO(List<IdentificationDocument> identificationDocuments){
        return identificationDocuments.stream()
                .map(identificationDocument -> toDTO(identificationDocument))
                .toList();
    }
}
