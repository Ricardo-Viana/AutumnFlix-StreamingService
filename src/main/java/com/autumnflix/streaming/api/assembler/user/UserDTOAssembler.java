package com.autumnflix.streaming.api.assembler.user;

import com.autumnflix.streaming.api.model.user.UserDTO;
import com.autumnflix.streaming.domain.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDTOAssembler {

    private ModelMapper modelMapper;

    public UserDTOAssembler(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public UserDTO toDTO(User user){
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> toCollectionDTO(List<User> users){
        return users.stream()
                .map(user -> toDTO(user))
                .toList();
    }
}
