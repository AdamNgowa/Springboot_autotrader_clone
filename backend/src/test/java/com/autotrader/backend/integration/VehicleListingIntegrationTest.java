package com.autotrader.backend.integration;

import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.repository.ConversationRepository;
import com.autotrader.backend.repository.FavoriteRepository;
import com.autotrader.backend.repository.MessageRepository;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.repository.VehicleImageRepository;
import com.autotrader.backend.repository.VehicleListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleListingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private VehicleImageRepository vehicleImageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Spring caches and reuses the ApplicationContext (and therefore the
        // same in-memory H2 database) across test classes that share an
        // identical @SpringBootTest configuration signature. That means rows
        // left behind by other integration test classes (Favorites,
        // Messaging, Images) can still be present when this class runs.
        //
        // messages/conversations/favorites/vehicle_images all hold
        // non-nullable foreign keys into vehicle_listings and/or users, so
        // child tables must be cleared before parent tables or deleteAll()
        // below throws a ConstraintViolationException.
        messageRepository.deleteAll();
        conversationRepository.deleteAll();
        favoriteRepository.deleteAll();
        vehicleImageRepository.deleteAll();
        vehicleListingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateListingForAuthenticatedUserAndRetrieveIt()
            throws Exception {

        String token = registerAndGetToken(
                "seller@example.com",
                "John",
                "Doe"
        );

        String listingRequest = createListingRequest(
                "2019 Toyota Corolla",
                "Well maintained Toyota Corolla",
                "1850000",
                2019,
                "Toyota",
                "Corolla",
                65000,
                "Nairobi"
        );

        String response = mockMvc.perform(
                        post("/listings")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(listingRequest)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title")
                        .value("2019 Toyota Corolla"))
                .andExpect(jsonPath("$.description")
                        .value("Well maintained Toyota Corolla"))
                .andExpect(jsonPath("$.price")
                        .value(1850000))
                .andExpect(jsonPath("$.year")
                        .value(2019))
                .andExpect(jsonPath("$.make")
                        .value("Toyota"))
                .andExpect(jsonPath("$.model")
                        .value("Corolla"))
                .andExpect(jsonPath("$.mileage")
                        .value(65000))
                .andExpect(jsonPath("$.fuelType")
                        .value("PETROL"))
                .andExpect(jsonPath("$.transmission")
                        .value("AUTOMATIC"))
                .andExpect(jsonPath("$.bodyType")
                        .value("SEDAN"))
                .andExpect(jsonPath("$.city")
                        .value("Nairobi"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long listingId = objectMapper
                .readTree(response)
                .get("id")
                .asLong();

        VehicleListing savedListing =
                vehicleListingRepository.findById(listingId)
                        .orElseThrow();

        assertNotNull(savedListing.getId());

        assertEquals(
                "2019 Toyota Corolla",
                savedListing.getTitle()
        );

        assertEquals(
                ListingStatus.ACTIVE,
                savedListing.getStatus()
        );

        User seller = userRepository.findByEmail(
                "seller@example.com"
        ).orElseThrow();

        assertEquals(
                seller.getId(),
                savedListing.getSeller().getId()
        );

        mockMvc.perform(
                        get("/listings/" + listingId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listingId))
                .andExpect(jsonPath("$.title")
                        .value("2019 Toyota Corolla"))
                .andExpect(jsonPath("$.seller.id")
                        .value(seller.getId()))
                .andExpect(jsonPath("$.seller.firstName")
                        .value("John"))
                .andExpect(jsonPath("$.seller.lastName")
                        .value("Doe"));
    }

    @Test
    void shouldRejectUnauthenticatedListingCreation()
            throws Exception {

        String listingRequest = createListingRequest(
                "2019 Toyota Corolla",
                "Well maintained Toyota Corolla",
                "1850000",
                2019,
                "Toyota",
                "Corolla",
                65000,
                "Nairobi"
        );

        mockMvc.perform(
                        post("/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(listingRequest)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowOwnerToUpdateListing()
            throws Exception {

        String token = registerAndGetToken(
                "owner@example.com",
                "John",
                "Owner"
        );

        Long listingId = createListing(token);

        String updateRequest = """
                {
                    "title": "2020 Toyota Corolla Updated",
                    "description": "Updated vehicle description",
                    "price": 1950000,
                    "year": 2020,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 70000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Mombasa"
                }
                """;

        mockMvc.perform(
                        put("/listings/" + listingId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listingId))
                .andExpect(jsonPath("$.title")
                        .value("2020 Toyota Corolla Updated"))
                .andExpect(jsonPath("$.price")
                        .value(1950000))
                .andExpect(jsonPath("$.year")
                        .value(2020))
                .andExpect(jsonPath("$.city")
                        .value("Mombasa"));

        VehicleListing updatedListing =
                vehicleListingRepository.findById(listingId)
                        .orElseThrow();

        assertEquals(
                "2020 Toyota Corolla Updated",
                updatedListing.getTitle()
        );

        assertEquals(
                ListingStatus.ACTIVE,
                updatedListing.getStatus()
        );
    }

    @Test
    void shouldRejectUpdateWhenAuthenticatedUserIsNotOwner()
            throws Exception {

        String ownerToken = registerAndGetToken(
                "owner@example.com",
                "John",
                "Owner"
        );

        String otherUserToken = registerAndGetToken(
                "other@example.com",
                "Jane",
                "Other"
        );

        Long listingId = createListing(ownerToken);

        String updateRequest = """
                {
                    "title": "Unauthorized Update",
                    "description": "This update should not succeed",
                    "price": 2000000,
                    "year": 2020,
                    "make": "Toyota",
                    "model": "Corolla",
                    "mileage": 70000,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "Nairobi"
                }
                """;

        mockMvc.perform(
                        put("/listings/" + listingId)
                                .header(
                                        "Authorization",
                                        "Bearer " + otherUserToken
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isForbidden());

        VehicleListing listing =
                vehicleListingRepository.findById(listingId)
                        .orElseThrow();

        assertEquals(
                "2019 Toyota Corolla",
                listing.getTitle()
        );
    }

    @Test
    void shouldAllowOwnerToDeleteListing()
            throws Exception {

        String token = registerAndGetToken(
                "delete-owner@example.com",
                "John",
                "Delete"
        );

        Long listingId = createListing(token);

        mockMvc.perform(
                        delete("/listings/" + listingId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());

        VehicleListing deletedListing =
                vehicleListingRepository.findById(listingId)
                        .orElseThrow();

        assertEquals(
                ListingStatus.DELETED,
                deletedListing.getStatus()
        );

        mockMvc.perform(
                        get("/listings/" + listingId)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDeleteWhenAuthenticatedUserIsNotOwner()
            throws Exception {

        String ownerToken = registerAndGetToken(
                "delete-owner@example.com",
                "John",
                "Owner"
        );

        String otherUserToken = registerAndGetToken(
                "delete-other@example.com",
                "Jane",
                "Other"
        );

        Long listingId = createListing(ownerToken);

        mockMvc.perform(
                        delete("/listings/" + listingId)
                                .header(
                                        "Authorization",
                                        "Bearer " + otherUserToken
                                )
                )
                .andExpect(status().isForbidden());

        VehicleListing listing =
                vehicleListingRepository.findById(listingId)
                        .orElseThrow();

        assertEquals(
                ListingStatus.ACTIVE,
                listing.getStatus()
        );
    }

    private Long createListing(String token)
            throws Exception {

        String listingRequest = createListingRequest(
                "2019 Toyota Corolla",
                "Well maintained Toyota Corolla",
                "1850000",
                2019,
                "Toyota",
                "Corolla",
                65000,
                "Nairobi"
        );

        String response = mockMvc.perform(
                        post("/listings")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(listingRequest)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper
                .readTree(response)
                .get("id")
                .asLong();
    }

    private String registerAndGetToken(
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
                """.formatted(
                firstName,
                lastName,
                email
        );

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

        return objectMapper
                .readTree(response)
                .get("token")
                .asText();
    }

    private String createListingRequest(
            String title,
            String description,
            String price,
            int year,
            String make,
            String model,
            int mileage,
            String city
    ) {
        return """
                {
                    "title": "%s",
                    "description": "%s",
                    "price": %s,
                    "year": %d,
                    "make": "%s",
                    "model": "%s",
                    "mileage": %d,
                    "fuelType": "PETROL",
                    "transmission": "AUTOMATIC",
                    "bodyType": "SEDAN",
                    "city": "%s"
                }
                """.formatted(
                title,
                description,
                price,
                year,
                make,
                model,
                mileage,
                city
        );
    }
}