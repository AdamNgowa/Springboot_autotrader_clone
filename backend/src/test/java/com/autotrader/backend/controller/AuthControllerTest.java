package com.autotrader.backend.controller;

import com.autotrader.backend.dto.auth.AuthResponse;
import com.autotrader.backend.dto.auth.LoginRequest;
import com.autotrader.backend.dto.auth.RegisterRequest;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
// addFilters = false stops the security filter chain from running on
// requests in this slice test. Controller-layer tests care about mapping,
// validation, and delegation to the service — not authentication, which
// is covered separately in 10.7 Security & Cross-Feature Testing.
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // AuthService is mocked so the controller can be tested in isolation,
    // without touching real registration/login business logic.
    @MockitoBean
    private AuthService authService;

    /*
     * Even with addFilters = false, @WebMvcTest still auto-registers any
     * Filter-typed bean it finds (that's part of its fixed infrastructure
     * scan, independent of the AuthController.class argument). Since
     * JwtAuthenticationFilter is a @Component implementing Filter, Spring
     * tries to construct the REAL filter bean to put in the context —
     * it just never gets invoked on a request.
     *
     * That construction still requires JwtService and
     * CustomUserDetailsService, so both must be mocked here purely to
     * satisfy the bean graph at startup. Their actual behavior is
     * irrelevant to this test class.
     */
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_withValidRequest_returnsCreatedResponse() throws Exception {

        // Arrange: create the request body that a real client would send.
        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "password": "Password123",
                    "phoneNumber": "0712345678"
                }
                """;

        // Arrange: simulate the service successfully registering the user.
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("test-jwt-token"));

        // Act + Assert: send a real HTTP-style request through MockMvc.
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        // Verify: the controller delegated the valid request to the service.
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // Arrange: intentionally omit required fields and use an invalid email.
        String requestBody = """
            {
                "firstName": "",
                "lastName": "",
                "email": "not-an-email",
                "password": "short",
                "phoneNumber": ""
            }
            """;

        // Act + Assert: Bean Validation should reject the request before
        // AuthService is ever reached.
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                // Field name matches ErrorResponse's "validationErrors" property,
                // as populated by GlobalExceptionHandler#handleValidationErrors.
                .andExpect(jsonPath("$.validationErrors").isArray());

        // Verify: invalid HTTP input must not reach the service layer.
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_withValidRequest_returnsOkResponse() throws Exception {

        // Arrange: create a valid login request.
        String requestBody = """
                {
                    "email": "john.doe@example.com",
                    "password": "Password123"
                }
                """;

        // Arrange: simulate a successful authentication response.
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("test-jwt-token"));

        // Act + Assert: send the login request and verify the HTTP contract.
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        // Verify: valid input is delegated to AuthService.
        verify(authService).login(any(LoginRequest.class));
    }
    @Test
    void login_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // Arrange: use an invalid email and omit the password.
        String requestBody = """
            {
                "email": "not-an-email",
                "password": ""
            }
            """;

        // Act + Assert: controller validation should reject the request.
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        // Verify: invalid input never reaches the authentication service.
        verify(authService, never()).login(any(LoginRequest.class));
    }

    }