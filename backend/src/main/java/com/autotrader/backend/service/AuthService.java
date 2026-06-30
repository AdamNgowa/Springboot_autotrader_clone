package com.autotrader.backend.service;

import com.autotrader.backend.dto.auth.AuthResponse;
import com.autotrader.backend.dto.auth.LoginRequest;
import com.autotrader.backend.dto.auth.RegisterRequest;
import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.EmailAlreadyExistsException;
import com.autotrader.backend.exception.InvalidCredentialsException;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email already registered!");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        //Store the hashed passwords
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //Server decides the user role
        user.setRole(UserRole.USER);

        //save user to database
        userRepository.save(user);

        //Jwt isn't implemented yet so return an empty token for now
        return new AuthResponse("");
    }

    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new InvalidCredentialsException("Invalid Email or password"));

        if(!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return new AuthResponse("");

    };
}
