package com.autotrader.backend.integration;

import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.repository.FavoriteRepository;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.repository.VehicleListingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FavoritesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        favoriteRepository.deleteAll();
        vehicleListingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldAllowAuthenticatedUserToAddFavorite() throws Exception {

        String token = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        Long listingId = createListing(
                registerAndGetToken(
                        "seller@example.com",
                        "John",
                        "Seller"
                )
        );

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/favorites/" + listingId + "/status")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));

        User buyer = userRepository.findByEmail("buyer@example.com")
                .orElseThrow();

        List<Favorite> favorites = favoriteRepository.findByUser(buyer);

        assertEquals(1, favorites.size());
        assertEquals(listingId, favorites.get(0).getVehicleListing().getId());
    }

    @Test
    void shouldBeIdempotentWhenFavoritingSameListingTwice() throws Exception {

        String token = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        Long listingId = createListing(
                registerAndGetToken(
                        "seller@example.com",
                        "John",
                        "Seller"
                )
        );

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        User buyer = userRepository.findByEmail("buyer@example.com")
                .orElseThrow();

        List<Favorite> favorites = favoriteRepository.findByUser(buyer);

        assertEquals(
                1,
                favorites.size(),
                "Favoriting the same listing twice should not create a duplicate row"
        );
    }

    @Test
    void shouldAllowOwnerToRemoveFavorite() throws Exception {

        String token = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        Long listingId = createListing(
                registerAndGetToken(
                        "seller@example.com",
                        "John",
                        "Seller"
                )
        );

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        delete("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/favorites/" + listingId + "/status")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false));

        User buyer = userRepository.findByEmail("buyer@example.com")
                .orElseThrow();

        assertEquals(0, favoriteRepository.findByUser(buyer).size());
    }

    @Test
    void shouldScopeFavoritesPerUserIndependently() throws Exception {

        String tokenA = registerAndGetToken(
                "buyer-a@example.com",
                "Alice",
                "BuyerA"
        );

        String tokenB = registerAndGetToken(
                "buyer-b@example.com",
                "Bob",
                "BuyerB"
        );

        Long listingId = createListing(
                registerAndGetToken(
                        "seller@example.com",
                        "John",
                        "Seller"
                )
        );

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + tokenB)
                )
                .andExpect(status().isNoContent());

        // User A removes their favorite; user B's favorite must be unaffected
        mockMvc.perform(
                        delete("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/favorites/" + listingId + "/status")
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(false));

        mockMvc.perform(
                        get("/favorites/" + listingId + "/status")
                                .header("Authorization", "Bearer " + tokenB)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite").value(true));
    }

    @Test
    void shouldReturnCurrentUserFavoritesList() throws Exception {

        String token = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        String sellerToken = registerAndGetToken(
                "seller@example.com",
                "John",
                "Seller"
        );

        Long firstListingId = createListing(sellerToken);
        Long secondListingId = createListing(sellerToken);

        mockMvc.perform(
                        post("/favorites/" + firstListingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post("/favorites/" + secondListingId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/favorites")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldRejectFavoritingNonexistentListing() throws Exception {

        String token = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        mockMvc.perform(
                        post("/favorites/999999")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectFavoritingSoftDeletedListing() throws Exception {

        String sellerToken = registerAndGetToken(
                "seller@example.com",
                "John",
                "Seller"
        );

        String buyerToken = registerAndGetToken(
                "buyer@example.com",
                "Jane",
                "Buyer"
        );

        Long listingId = createListing(sellerToken);

        // Soft-delete the listing as its owner (sets status to DELETED)
        mockMvc.perform(
                        delete("/listings/" + listingId)
                                .header("Authorization", "Bearer " + sellerToken)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        post("/favorites/" + listingId)
                                .header("Authorization", "Bearer " + buyerToken)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticatedFavoriteActions() throws Exception {

        String sellerToken = registerAndGetToken(
                "seller@example.com",
                "John",
                "Seller"
        );

        Long listingId = createListing(sellerToken);

        mockMvc.perform(post("/favorites/" + listingId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/favorites/" + listingId))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/favorites"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/favorites/" + listingId + "/status"))
                .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private Long createListing(String token) throws Exception {

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
                                .header("Authorization", "Bearer " + token)
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