package com.autotrader.backend.service;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.AuthenticatedUserNotFoundException;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// Marks this class as a Spring Service component so Spring manages its lifecycle as a bean
@Service
public class CurrentUserService {
    // Injecting UserRepository to query user data from the database
    private final UserRepository userRepository;

    // Constructor injection: Spring automatically injects the UserRepository dependency
    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the database record for the user currently logged into the system.
     *
     * @return User the authenticated user's entity
     * @throws AuthenticatedUserNotFoundException if no user is logged in or user record is missing in DB
     */
    public User getAuthenticatedUser() {
        // Retrieve the current thread's SecurityContext and get its active Authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication object is missing or if the user is not authenticated (e.g., anonymous)
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticatedUserNotFoundException(
                    "Authenticated user not found"
            );
        }

        // Extract the principal identifier (configured as the user's email address) from the authentication object
        String email = authentication.getName();

        // Query the database for the user by email; throw custom exception if not found
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticatedUserNotFoundException("Authenticated user not found"));
    }
}