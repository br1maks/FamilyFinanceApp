package com.maximilian.familyfinance.service;

import com.maximilian.familyfinance.entity.User;
import com.maximilian.familyfinance.exception.auth.UserNotAuthenticatedException;
import com.maximilian.familyfinance.exception.user.UserNotFoundException;
import com.maximilian.familyfinance.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentAuthorizedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        throw new UserNotAuthenticatedException();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void ban(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setIsAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unban(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new AccessDeniedException("You can't ban admin");
        }

        user.setIsAccountNonLocked(true);
        userRepository.save(user);
    }

    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getUsername().equalsIgnoreCase("admin")) {
            throw new AccessDeniedException("You can't delete admin user");
        }

        userRepository.delete(user);
    }
}
