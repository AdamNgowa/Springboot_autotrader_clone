package com.autotrader.backend.security;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// @Service is a specialized flavor of @Component. It tells Spring to manage this class as a Bean
// and marks it as a layer containing core business logic.
// 'implements UserDetailsService' is a contract promising Spring that we will provide a user lookup method.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // 1. DECLARING THE PERMANENT SLOT (DEPENDENCY)
    private final UserRepository userRepository;

    // 2. CONSTRUCTOR INJECTION
    // Spring automatically supplies the UserRepository instance from its database context layer.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // @Override fulfills the interface contract. This method is called automatically
    // by your AuthenticationProvider and your JwtAuthenticationFilter.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Step 1: Query the database using our Repository layer to find a user by their email.
        // If no record exists, immediately halt and throw a "UsernameNotFoundException".
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Step 2: Translate our custom Database Entity (User) into Spring Security's native passport (UserDetails).
        // Spring Security doesn't understand our custom entity classes, so we use Spring's built-in
        // User Builder to extract our user's email, hashed password, and role name, compiling it into a format Spring understands.
        return org.springframework.security.core.userdetails.User
                // Maps your database user's email as Spring Security's official identifier (the "username").
                .withUsername(user.getEmail())

                // Feeds your database user's encrypted/hashed password into Spring Security.
                // The AuthenticationProvider will use this to compare against the password entered during login.
                .password(user.getPassword())

                .roles(user.getRole().name()) // Extracts role string (e.g., "USER" or "ADMIN")
                .build(); // Constructs and returns the final UserDetails passport object
    }
}