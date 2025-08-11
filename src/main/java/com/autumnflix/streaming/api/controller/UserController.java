package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.user.UserDTOAssembler;
import com.autumnflix.streaming.api.assembler.user.UserInputDTODisassembler;
import com.autumnflix.streaming.api.model.user.UserDTO;
import com.autumnflix.streaming.api.model.user.UserInputDTO;
import com.autumnflix.streaming.domain.model.User;
import com.autumnflix.streaming.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    private UserDTOAssembler userDTOAssembler;

    private UserInputDTODisassembler userInputDTODisassembler;

    public UserController(UserService userService,
                          UserDTOAssembler userDTOAssembler,
                          UserInputDTODisassembler userInputDTODisassembler) {
        this.userService = userService;
        this.userDTOAssembler = userDTOAssembler;
        this.userInputDTODisassembler = userInputDTODisassembler;
    }

    @GetMapping
    public List<UserDTO> getAll(){
        return userDTOAssembler.toCollectionDTO(userService.getAll());
    }

    @GetMapping("{userId}")
    public UserDTO getById(@PathVariable("userId") Long userId){
        return userDTOAssembler.toDTO(userService.getUser(userId));
    }

    @GetMapping("/email/{userEmail}")
    public UserDTO getByEmail(@PathVariable("userEmail") String email){
        return userDTOAssembler.toDTO(userService.getUserByEmail(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO add(@RequestBody @Valid UserInputDTO userInputDTO){
        User user = userInputDTODisassembler.toEntityObject(userInputDTO);
        return userDTOAssembler.toDTO(userService.insert(user));
    }

    @PutMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@PathVariable("userId") Long userId,@RequestBody @Valid UserInputDTO userInputDTO){
        User existingUser = userService.getUser(userId);

        userInputDTODisassembler.copyToEntityObject(userInputDTO, existingUser);

        return userDTOAssembler.toDTO(userService.insert(existingUser));
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") Long userId){
        userService.delete(userId);
    }
}
