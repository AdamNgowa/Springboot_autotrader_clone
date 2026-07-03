package com.autotrader.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component tells Spring Boot to automatically manage this class as a Bean
// 'extends OncePerRequestFilter' gives us complex pre-built web logic ensuring this filter runs exactly ONCE per incoming request
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 1. DECLARING PERMANENT SLOTS (DEPENDENCIES)
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // 2. CONSTRUCTOR INJECTION
    // Spring automatically supplies instances of JwtService and CustomUserDetailsService from its memory container
    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // @Override replaces the parent class's default web filter method with our custom JWT authentication logic
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,   // The incoming network packet from the user
            @NonNull HttpServletResponse response, // The data our server sends back to the user
            @NonNull FilterChain filterChain)      // The pipeline of other security bouncers waiting in line

            throws ServletException, IOException {

        // Step 1: Reach into the incoming request and pull out the header named "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // Step 2: Check if a token was even provided
        // If there's no header, or if it doesn't start with "Bearer ", the user isn't trying to log in via token.
        // We hand the request over to the next filter in line and stop executing this method immediately.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Strip away the prefix "Bearer " (which is exactly 7 characters long) to get the raw JWT string
        String jwt = authHeader.substring(7);

        // Step 4: Use our permanent slot 'jwtService' to decode the token and pull out the user's email
        String email = jwtService.extractUsername(jwt);

        // Step 5: Perform a sanity check
        // Ensure we actually extracted an email AND that this user isn't already logged into Spring's security system.
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6: Use our 'userDetailsService' to look up the user in our real database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            // Step 7: Check if the token matches our database user record and hasn't expired yet
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Step 8: Create Spring's official internal "Access Pass" passport (Authentication Token)
                // We pass 'null' for the password because they've already proven who they are via the JWT.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // Attach their security roles (e.g., ROLE_USER)
                        );

                // Step 9: Stamp extra network metadata onto the passport (like IP address and session info)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Step 10: The Golden Moment! Slide this official passport into Spring's security vault.
                // The entire application now acknowledges this user as officially logged in and verified.
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // Step 11: Push the request forward down the pipeline to the next filter or your Controller endpoint.
        filterChain.doFilter(request, response);
    }
}