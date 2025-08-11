package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getUser(Long userId);

    User getUserByEmail(String email);

    User insert(User user);

    void delete(Long userId);

    void associateWatchedEntertainmentWork(Long userId, Long entertainmentWorkId);

    void disassociateWatchedEntertainmentWork(Long userId, Long entertainmentWorkId);

    void associateFavoriteEntertainmentWork(Long userId, Long entertainmentWorkId);

    void disassociateFavoriteEntertainmentWork(Long userId, Long entertainmentWorkId);

    void canUserWatchEntertainmentWork(Long userId);

    void checkDuplicatedEmail(User user);

    void checkDuplicatedIdentificationDocumentValue(User user);

    void checkDuplicatedFavoriteEntertainmentWork(User user, EntertainmentWork entertainmentWork);
}
