package com.autotrader.backend.integration;

import com.autotrader.backend.TestcontainersConfiguration;
import com.autotrader.backend.repository.ConversationRepository;
import com.autotrader.backend.repository.FavoriteRepository;
import com.autotrader.backend.repository.MessageRepository;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.repository.VehicleImageRepository;
import com.autotrader.backend.repository.VehicleListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for every MockMvc-based integration test.
 *
 * All subclasses share the SAME annotation signature, so Spring caches ONE
 * ApplicationContext (and therefore ONE PostgreSQL container) for the whole group.
 * The price of sharing is that rows leak between test classes, so this class
 * wipes every table before each test, children first (foreign keys).
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
public abstract class IntegrationTestSupport {

    protected static final String AUTH = "Authorization";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected VehicleListingRepository vehicleListingRepository;

    @Autowired
    protected VehicleImageRepository vehicleImageRepository;

    @Autowired
    protected FavoriteRepository favoriteRepository;

    @Autowired
    protected ConversationRepository conversationRepository;

    @Autowired
    protected MessageRepository messageRepository;

    // Named cleanDatabase (not setUp) so a subclass @BeforeEach never overrides it.
    // Superclass @BeforeEach methods always run before subclass ones.
    @BeforeEach
    protected void cleanDatabase() {
        messageRepository.deleteAll();
        conversationRepository.deleteAll();
        favoriteRepository.deleteAll();
        vehicleImageRepository.deleteAll();
        vehicleListingRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }

    protected String registerAndGetToken(
            String email,
            String firstName,
            String lastName
    ) throws Exception {

        String registrationRequest = """
                {
                    "firstName": "%s",
                    "lastName": "%s",
                    "email": "%s",
                    "password": "SecurePassword123!",
                    "phoneNumber": "+254712345678"
                }
                """.formatted(firstName, lastName, email);

        String response = mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registrationRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    protected Long createListing(String token) throws Exception {
        return createListing(token, "2019 Toyota Corolla");
    }

    protected Long createListing(String token, String title) throws Exception {

        String listingRequest = """
                {
                    "title": "%s",
                    "description": "Well maintained vehicle",
                    "price": 1850000,
                    "year": 2019,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 65000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Nairobi"
                }
                """.formatted(title);

        String response = mockMvc.perform(
                        post("/listings")
                                .header(AUTH, bearer(token))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(listingRequest)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
