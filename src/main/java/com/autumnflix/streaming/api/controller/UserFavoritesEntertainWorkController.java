package com.autumnflix.streaming.api.controller;

import com.autumnflix.streaming.api.assembler.entertainmentWork.EntertainmentWorkDtoAssembler;
import com.autumnflix.streaming.api.model.entertainmentWork.EntertainmentWorkDto;
import com.autumnflix.streaming.domain.model.User;
import com.autumnflix.streaming.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users/{userId}/favorite")
public class UserFavoritesEntertainWorkController {

    private UserService userService;

    private EntertainmentWorkDtoAssembler entertainmentWorkDtoAssembler;

    public UserFavoritesEntertainWorkController(UserService userService, EntertainmentWorkDtoAssembler entertainmentWorkDtoAssembler){
        this.userService = userService;
        this.entertainmentWorkDtoAssembler = entertainmentWorkDtoAssembler;
    }

    @GetMapping
    public List<EntertainmentWorkDto> getAllByUserId(@PathVariable("userId") Long userId){
        User user = userService.getUser(userId);

        return entertainmentWorkDtoAssembler.toCollectionDto(user.getFavoriteEntertainWorks());
    }

    @PutMapping("/{entertainmentWorkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void associate(@PathVariable("userId") Long userId,
                          @PathVariable("entertainmentWorkId") Long entertainmentWorkId){
        userService.associateFavoriteEntertainmentWork(userId, entertainmentWorkId);
    }

    @DeleteMapping("/{entertainmentWorkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disassociate(@PathVariable("userId") Long userId,
                             @PathVariable("entertainmentWorkId") Long entertainmentWorkId){
        userService.disassociateFavoriteEntertainmentWork(userId, entertainmentWorkId);
    }
}
