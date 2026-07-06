package com.autotrader.backend.controller;

import com.autotrader.backend.dto.auth.AuthResponse;
import com.autotrader.backend.dto.auth.LoginRequest;
import com.autotrader.backend.dto.auth.RegisterRequest;
import com.autotrader.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController combines @Controller and @ResponseBody. It tells Spring Boot that this class is an API entry point
// and that all return data from these methods should be automatically converted into JSON packets for the client.
@RestController
// @RequestMapping sets up the base URL prefix for this entire file. Every endpoint inside will start with "/auth".
@RequestMapping("/auth")
public class AuthController {

    // 1. DECLARING PERMANENT SLOTS (DEPENDENCIES)
    private final AuthService authService;

    // 2. CONSTRUCTOR INJECTION
    // Spring looks in its memory context, finds our AuthService component bean, and injects it here.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ==========================================
    // API ENDPOINTS
    // ==========================================

    // @PostMapping maps this method to HTTP POST requests hitting "/auth/register".
    // ResponseEntity<AuthResponse> is a wrapper object that lets us control both the body data and the HTTP status code.
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            // @RequestBody acts like a funnel. It intercepts the incoming raw JSON text payload from the client's
            // request body and instructs Jackson (Spring's JSON parser) to parse it directly into a Java 'RegisterRequest' object.
            @RequestBody RegisterRequest request
    ) {
        // Delegate the core registration business logic task to our injected AuthService
        AuthResponse response = authService.register(request);

        // Build and return the network response: Set HTTP Status to 201 Created and attach our registration token body
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // @PostMapping maps this method to HTTP POST requests hitting "/auth/login".
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            // Intercepts and parses the raw JSON login credentials body into a 'LoginRequest' object
            @RequestBody LoginRequest request
    ) {
        // Pass the credentials to our service layer to verify the email/password combination and mint a token
        AuthResponse response = authService.login(request);

        // ResponseEntity.ok() is a shorthand utility method that builds a response with an HTTP Status of 200 OK,
        // seamlessly packaging our auth token payload into the response body.
        return ResponseEntity.ok(response);
    }
}