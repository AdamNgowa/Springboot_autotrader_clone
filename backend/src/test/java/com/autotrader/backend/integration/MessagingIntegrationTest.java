package com.autotrader.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Buyer/seller messaging through the real stack. The key property under test is
 * PARTICIPANT-ONLY ACCESS: an outsider (or someone guessing IDs) must never read
 * or write a conversation they are not part of.
 */
class MessagingIntegrationTest extends IntegrationTestSupport {

    private String sellerToken;
    private String buyerToken;
    private String outsiderToken;
    private Long listingId;

    @BeforeEach
    void setUpUsersAndListing() throws Exception {
        sellerToken = registerAndGetToken("seller@example.com", "Sam", "Seller");
        buyerToken = registerAndGetToken("buyer@example.com", "Bella", "Buyer");
        outsiderToken = registerAndGetToken("outsider@example.com", "Oscar", "Outsider");
        listingId = createListing(sellerToken);
    }

    // ==========================================
    // STARTING A CONVERSATION
    // ==========================================

    @Test
    void shouldStartConversationFromListing() throws Exception {
        Long buyerId = userId("buyer@example.com");
        Long sellerId = userId("seller@example.com");

        mockMvc.perform(post("/conversations")
                        .header(AUTH, bearer(buyerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversationBody(listingId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.listingId").value(listingId))
                .andExpect(jsonPath("$.buyerId").value(buyerId))
                .andExpect(jsonPath("$.sellerId").value(sellerId));
    }

    @Test
    void shouldReturnExistingConversationWhenStartedTwice() throws Exception {
        long first = startConversation(buyerToken).get("id").asLong();
        long second = startConversation(buyerToken).get("id").asLong();

        assertThat(second).isEqualTo(first);
        assertThat(conversationRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldForbidSellerFromMessagingThemselves() throws Exception {
        mockMvc.perform(post("/conversations")
                        .header(AUTH, bearer(sellerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversationBody(listingId)))
                .andExpect(status().isForbidden());

        assertThat(conversationRepository.count()).isZero();
    }

    @Test
    void shouldReturn404WhenStartingConversationOnMissingListing() throws Exception {
        mockMvc.perform(post("/conversations")
                        .header(AUTH, bearer(buyerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversationBody(999_999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectConversationRequestWithoutListingId() throws Exception {
        mockMvc.perform(post("/conversations")
                        .header(AUTH, bearer(buyerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // SENDING AND READING MESSAGES
    // ==========================================

    @Test
    void shouldLetBothParticipantsExchangeAndReadMessagesInOrder() throws Exception {
        long conversationId = startConversation(buyerToken).get("id").asLong();

        sendMessage(buyerToken, conversationId, "Is it still available?");
        sendMessage(sellerToken, conversationId, "Yes, it is.");

        Long buyerId = userId("buyer@example.com");
        Long sellerId = userId("seller@example.com");

        // Both participants see the same two messages, oldest first.
        for (String token : new String[]{buyerToken, sellerToken}) {
            mockMvc.perform(get("/conversations/" + conversationId + "/messages")
                            .header(AUTH, bearer(token)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].content")
                            .value("Is it still available?"))
                    .andExpect(jsonPath("$.content[0].senderId").value(buyerId))
                    .andExpect(jsonPath("$.content[1].content")
                            .value("Yes, it is."))
                    .andExpect(jsonPath("$.content[1].senderId").value(sellerId));
        }
    }

    @Test
    void shouldLetSellerOpenConversationStartedByBuyer() throws Exception {
        long conversationId = startConversation(buyerToken).get("id").asLong();

        mockMvc.perform(get("/conversations/" + conversationId)
                        .header(AUTH, bearer(sellerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(conversationId));
    }

    @Test
    void shouldRejectBlankMessageContent() throws Exception {
        long conversationId = startConversation(buyerToken).get("id").asLong();

        mockMvc.perform(post("/conversations/" + conversationId + "/messages")
                        .header(AUTH, bearer(buyerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("   ")))
                .andExpect(status().isBadRequest());

        assertThat(messageRepository.count()).isZero();
    }

    @Test
    void shouldRejectMessageLongerThan2000Characters() throws Exception {
        long conversationId = startConversation(buyerToken).get("id").asLong();

        mockMvc.perform(post("/conversations/" + conversationId + "/messages")
                        .header(AUTH, bearer(buyerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("x".repeat(2001))))
                .andExpect(status().isBadRequest());

        assertThat(messageRepository.count()).isZero();
    }

    // ==========================================
    // INBOX
    // ==========================================

    @Test
    void shouldListOnlyConversationsTheUserParticipatesIn() throws Exception {
        startConversation(buyerToken);

        for (String participantToken : new String[]{buyerToken, sellerToken}) {
            mockMvc.perform(get("/conversations")
                            .header(AUTH, bearer(participantToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1));
        }

        mockMvc.perform(get("/conversations")
                        .header(AUTH, bearer(outsiderToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    // ==========================================
    // PARTICIPANT-ONLY ACCESS
    // ==========================================

    @Test
    void shouldBlockOutsiderFromReadingSendingAndOpeningConversation() throws Exception {
        long conversationId = startConversation(buyerToken).get("id").asLong();
        sendMessage(buyerToken, conversationId, "Private message");

        mockMvc.perform(get("/conversations/" + conversationId)
                        .header(AUTH, bearer(outsiderToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/conversations/" + conversationId + "/messages")
                        .header(AUTH, bearer(outsiderToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/conversations/" + conversationId + "/messages")
                        .header(AUTH, bearer(outsiderToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("I should not be able to write this")))
                .andExpect(status().isForbidden());

        // Only the original message exists: the outsider's write was not persisted.
        assertThat(messageRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldAnswerForbiddenForNonexistentConversationIds() throws Exception {
        // Guessing IDs must not reveal whether a conversation exists:
        // "missing" and "not yours" look identical to the caller.
        mockMvc.perform(get("/conversations/999999")
                        .header(AUTH, bearer(buyerToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/conversations/999999/messages")
                        .header(AUTH, bearer(buyerToken)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private JsonNode startConversation(String token) throws Exception {
        String body = mockMvc.perform(post("/conversations")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversationBody(listingId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body);
    }

    private void sendMessage(String token, long conversationId, String content)
            throws Exception {

        mockMvc.perform(post("/conversations/" + conversationId + "/messages")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody(content)))
                .andExpect(status().isOk());
    }

    private String conversationBody(Long forListingId) throws Exception {
        return objectMapper.writeValueAsString(Map.of("listingId", forListingId));
    }

    private String messageBody(String content) throws Exception {
        return objectMapper.writeValueAsString(Map.of("content", content));
    }

    private Long userId(String email) {
        return userRepository.findByEmail(email).orElseThrow().getId();
    }
}
