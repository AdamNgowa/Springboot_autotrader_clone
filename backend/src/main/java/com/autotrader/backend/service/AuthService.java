package com.autotrader.backend.service;

import com.autotrader.backend.dto.auth.AuthResponse;
import com.autotrader.backend.dto.auth.LoginRequest;
import com.autotrader.backend.dto.auth.RegisterRequest;
import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.EmailAlreadyExistsException;
import com.autotrader.backend.exception.InvalidCredentialsException;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    // 1. DECLARING PERMANENT SLOTS (DEPENDENCIES)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 2. CONSTRUCTOR INJECTION
    // Spring grabs the database data interface, the password bean, and our token factory and supplies them here.
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Processes new account registrations
    public AuthResponse register(RegisterRequest request){
        // Step 1: Check if the email is already taken in the system. If it is, halt registration.
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already registered!");
        }

        // Step 2: Create a brand new blank instance of our User entity database row model
        User user = new User();

        // Step 3: Populate the blank user record with data incoming from the registration form request
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        // Step 4: Encrypt the plain-text password using BCrypt before writing it anywhere near the database
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Step 5: System security default rule overrides: hardcode role to standard USER for self-registrations
        user.setRole(UserRole.USER);

        // Step 6: Commit the fully configured user entity record safely into the physical database tables
        userRepository.save(user);

        // Step 7: Fix the placeholder logic from before! Generate a live token for the user upon sign-up
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    // Processes account login verifications
    public AuthResponse login(LoginRequest request){

        // Step 1: Try to look up the user record by the provided email. If it isn't found, halt with an error.
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new InvalidCredentialsException("Invalid Email or password"));

        // Step 2: Use passwordEncoder.matches() to safely verify if the input password matches the database hash.
        // If it returns false, throw a credential exception.
        if(!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Step 3: Now that credentials match, hand the user's email to JwtService to manufacture an official token
        String token = jwtService.generateToken(user.getEmail());

        // Step 4: Wrap the final authentication token up inside an AuthResponse data structure object and return it
        return new AuthResponse(token);
    }
}