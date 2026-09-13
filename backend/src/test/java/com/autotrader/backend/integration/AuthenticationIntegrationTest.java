package com.autotrader.backend.integration;

import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserAndAccessProtectedEndpointWithReturnedJwt()
            throws Exception {

        String registrationRequest = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "integration@example.com",
                    "password": "SecurePassword123!",
                    "phoneNumber": "+254712345678"
                }
                """;

        String registrationResponse = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(registrationResponse);

        User savedUser = userRepository.findByEmail(
                "integration@example.com"
        ).orElseThrow();

        assertNotNull(savedUser.getId());

        assertEquals(
                "integration@example.com",
                savedUser.getEmail()
        );

        assertEquals(
                UserRole.USER,
                savedUser.getRole()
        );

        assertNotEquals(
                "SecurePassword123!",
                savedUser.getPassword()
        );

        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email")
                        .value("integration@example.com"))
                .andExpect(jsonPath("$.phoneNumber")
                        .value("+254712345678"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldRejectUnauthenticatedAccessToProtectedEndpoint()
            throws Exception {

        mockMvc.perform(
                        get("/users/me")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLoginSuccessfullyAndAccessProtectedEndpoint()
            throws Exception {

        String registrationRequest = """
                {
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "email": "jane@example.com",
                    "password": "SecurePassword123!",
                    "phoneNumber": "+254712345678"
                }
                """;

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isCreated());

        String loginRequest = """
                {
                    "email": "jane@example.com",
                    "password": "SecurePassword123!"
                }
                """;

        String loginResponse = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(loginResponse);

        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email")
                        .value("jane@example.com"))
                .andExpect(jsonPath("$.firstName")
                        .value("Jane"))
                .andExpect(jsonPath("$.lastName")
                        .value("Doe"))
                .andExpect(jsonPath("$.role")
                        .value("USER"));
    }

    @Test
    void shouldRejectDuplicateRegistration()
            throws Exception {

        String registrationRequest = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "duplicate@example.com",
                    "password": "SecurePassword123!",
                    "phoneNumber": "+254712345678"
                }
                """;

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectLoginWithIncorrectPassword()
            throws Exception {

        String registrationRequest = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "wrong-password@example.com",
                    "password": "SecurePassword123!",
                    "phoneNumber": "+254712345678"
                }
                """;

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isCreated());

        String loginRequest = """
                {
                    "email": "wrong-password@example.com",
                    "password": "WrongPassword123!"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectLoginWhenUserDoesNotExist()
            throws Exception {

        String loginRequest = """
                {
                    "email": "does-not-exist@example.com",
                    "password": "SecurePassword123!"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest)
                )
                .andExpect(status().isUnauthorized());
    }

    private String extractToken(String response) throws Exception {
        return objectMapper
                .readTree(response)
                .get("token")
                .asText();
    }
}