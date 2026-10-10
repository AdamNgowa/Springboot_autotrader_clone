package com.autotrader.backend.service;

import com.autotrader.backend.dto.user.ChangePasswordRequest;
import com.autotrader.backend.dto.user.UpdateProfileRequest;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.IncorrectCurrentPasswordException;
import com.autotrader.backend.exception.UserNotFoundException;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    // Updates the editable profile fields. Email, role and password are untouched.
    @Transactional
    public User updateProfile(User authenticatedUser, UpdateProfileRequest request) {
        User user = getUserById(authenticatedUser.getId());

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());

        return userRepository.save(user);
    }

    // Verifies the current password against the stored BCrypt hash, then stores the new hash.
    @Transactional
    public void changePassword(User authenticatedUser, ChangePasswordRequest request) {
        User user = getUserById(authenticatedUser.getId());

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new IncorrectCurrentPasswordException(
                    "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}