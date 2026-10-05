package com.autotrader.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Public seller profile: viewable WITHOUT authentication, exposes only safe
 * fields, and lists only ACTIVE listings with pagination.
 */
class SellerProfileIntegrationTest extends IntegrationTestSupport {

    private String sellerToken;
    private Long sellerId;

    @BeforeEach
    void setUpSeller() throws Exception {
        sellerToken = registerAndGetToken("seller@example.com", "Sam", "Seller");
        sellerId = userRepository.findByEmail("seller@example.com")
                .orElseThrow()
                .getId();
    }

    @Test
    void shouldExposeOnlyPublicFieldsToAnonymousVisitor() throws Exception {
        mockMvc.perform(get("/users/" + sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sellerId))
                .andExpect(jsonPath("$.firstName").value("Sam"))
                .andExpect(jsonPath("$.lastName").value("Seller"))
                .andExpect(jsonPath("$.phoneNumber").value("+254712345678"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void shouldReturn404ForUnknownSeller() throws Exception {
        mockMvc.perform(get("/users/999999"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/999999/listings"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListOnlyActiveListingsOfThatSeller() throws Exception {
        Long activeListing = createListing(sellerToken, "Active Listing");
        Long deletedListing = createListing(sellerToken, "Deleted Listing");

        mockMvc.perform(delete("/listings/" + deletedListing)
                        .header(AUTH, bearer(sellerToken)))
                .andExpect(status().isNoContent());

        // A different seller's listing must never appear on this profile.
        String otherToken = registerAndGetToken("other@example.com", "Olga", "Other");
        createListing(otherToken, "Someone Else's Listing");

        mockMvc.perform(get("/users/" + sellerId + "/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(activeListing))
                .andExpect(jsonPath("$.content[0].seller.firstName").value("Sam"));
    }

    @Test
    void shouldPaginateSellerListings() throws Exception {
        createListing(sellerToken, "Listing 1");
        createListing(sellerToken, "Listing 2");
        createListing(sellerToken, "Listing 3");

        mockMvc.perform(get("/users/" + sellerId + "/listings")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/users/" + sellerId + "/listings")
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldReturnEmptyPageForSellerWithoutListings() throws Exception {
        mockMvc.perform(get("/users/" + sellerId + "/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}
