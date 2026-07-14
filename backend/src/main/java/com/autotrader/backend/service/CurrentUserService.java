package com.autotrader.backend.service;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.AuthenticatedUserNotFoundException;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new AuthenticatedUserNotFoundException("Authenticated user not found"));
    }
}
