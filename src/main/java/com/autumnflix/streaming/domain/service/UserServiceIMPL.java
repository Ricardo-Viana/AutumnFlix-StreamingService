package com.autumnflix.streaming.domain.service;

import com.autumnflix.streaming.domain.exception.BusinessException;
import com.autumnflix.streaming.domain.exception.EntityBeingUsedException;
import com.autumnflix.streaming.domain.exception.UserNotFoundException;
import com.autumnflix.streaming.domain.model.Credit;
import com.autumnflix.streaming.domain.model.EntertainmentWork;
import com.autumnflix.streaming.domain.model.Plan;
import com.autumnflix.streaming.domain.model.User;
import com.autumnflix.streaming.domain.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.autumnflix.streaming.domain.model.PlanType.PREMIUM;

@Service
public class UserServiceIMPL implements UserService {

    private UserRepository userRepository;

    private PlanService planService;

    private EntertainmentWorkService entertainmentWorkService;

    public UserServiceIMPL(UserRepository userRepository, PlanService planService, EntertainmentWorkService entertainmentWorkService){
        this.userRepository = userRepository;
        this.planService = planService;
        this.entertainmentWorkService = entertainmentWorkService;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s doesn't exist", email)));
    }

    @Override
    @Transactional
    public User insert(User user) {
        userRepository.detach(user);

        Plan plan = planService.getPlan(user.getPlan().getId());
        user.setPlan(plan);

        Credit credit = new Credit();
        credit.setQuantity(user.getPlan().getNumCredits());
        LocalDateTime ldt  = LocalDateTime.now();
        ZoneOffset offset = ZoneOffset.UTC;
        credit.setDate(ldt.atOffset(offset));
        user.setCredit(credit);

        checkDuplicatedEmail(user);
        checkDuplicatedIdentificationDocumentValue(user);

        return userRepository.save(user);
    }

    @Override
    public void checkDuplicatedEmail(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser.isPresent() && !user.equals(existingUser.get())){
            throw new EntityBeingUsedException("User is already registered");
        }
    }

    @Override
    public void checkDuplicatedIdentificationDocumentValue(User user) {
        Optional<User> existingUser = userRepository.findByIdentificationDocument_Value(user.getIdentificationDocument().getValue());

        if(existingUser.isPresent() && !user.equals(existingUser.get())){
            throw new EntityBeingUsedException("User is already registered");
        }
    }

    @Override
    public void checkDuplicatedFavoriteEntertainmentWork(User user, EntertainmentWork entertainmentWork) {
        if(user.getFavoriteEntertainWorks().contains(entertainmentWork)){
            throw new EntityBeingUsedException("EntertainmentWork is already favorite");
        }
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        User user = getUser(userId);

        try {
            userRepository.delete(user);
        }catch (DataIntegrityViolationException e){
            throw new EntityBeingUsedException("User", userId);
        }
    }

    @Override
    @Transactional
    public void associateWatchedEntertainmentWork(Long userId, Long entertainmentWorkId) {
        canUserWatchEntertainmentWork(userId);

        User user = getUser(userId);
        EntertainmentWork entertainmentWork = entertainmentWorkService.getEntertainmentWork(entertainmentWorkId);

        user.addWatchedEntertainWorks(entertainmentWork);
    }

    @Override
    @Transactional
    public void disassociateWatchedEntertainmentWork(Long userId, Long entertainmentWorkId) {
        User user = getUser(userId);
        EntertainmentWork entertainmentWork = entertainmentWorkService.getEntertainmentWork(entertainmentWorkId);

        user.removeWatchedEntertainWorks(entertainmentWork);
    }

    @Override
    @Transactional
    public void associateFavoriteEntertainmentWork(Long userId, Long entertainmentWorkId) {
        User user = getUser(userId);
        EntertainmentWork entertainmentWork = entertainmentWorkService.getEntertainmentWork(entertainmentWorkId);

        checkDuplicatedFavoriteEntertainmentWork(user, entertainmentWork);

        user.addFavoritesEntertainWorks(entertainmentWork);
    }

    @Override
    @Transactional
    public void disassociateFavoriteEntertainmentWork(Long userId, Long entertainmentWorkId) {
        User user = getUser(userId);
        EntertainmentWork entertainmentWork = entertainmentWorkService.getEntertainmentWork(entertainmentWorkId);

        user.removeFavoritesEntertainWorks(entertainmentWork);
    }

    @Override
    @Transactional
    public void canUserWatchEntertainmentWork(Long userId) {
        User user = getUser(userId);

        LocalDateTime ldt  = LocalDateTime.now();
        ZoneOffset offset = ZoneOffset.UTC;
        long daysDifference = ChronoUnit.DAYS.between(user.getCredit().getDate(),ldt.atOffset(offset)); // Checks if the current date is at least one day after the date the user had his credits changed

        if(user.getCredit().getQuantity() == 0 && daysDifference < 1){
            throw new BusinessException("User doesn't have enough credits available");
        }
        else{
            if(!user.getPlan().getType().equals(PREMIUM)){
                if(daysDifference >= 1){
                    user.getCredit().setQuantity(user.getPlan().getNumCredits() - 1);
                    user.getCredit().setDate(ldt.atOffset(offset));
                }
                else user.getCredit().setQuantity(user.getCredit().getQuantity() - 1);
            }
        }
        userRepository.save(user);
    }
}
