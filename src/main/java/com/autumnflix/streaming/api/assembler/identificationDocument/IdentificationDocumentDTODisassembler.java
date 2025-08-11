package com.autumnflix.streaming.api.assembler.identificationDocument;

import com.autumnflix.streaming.api.model.identificationDocument.IdentificationDocumentDTO;
import com.autumnflix.streaming.domain.model.IdentificationDocument;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class IdentificationDocumentDTODisassembler {

    private ModelMapper modelMapper;

    public IdentificationDocumentDTODisassembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public IdentificationDocument toEntityObject(IdentificationDocumentDTO identificationDocumentDTO){
        return modelMapper.map(identificationDocumentDTO, IdentificationDocument.class);
    }

    public void copyToEntityObject(IdentificationDocumentDTO source, IdentificationDocument destination){
        modelMapper.map(source, destination);
    }
}
