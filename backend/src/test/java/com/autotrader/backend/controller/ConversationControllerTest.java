package com.autotrader.backend.controller;

import com.autotrader.backend.dto.messaging.ConversationResponse;
import com.autotrader.backend.dto.messaging.CreateConversationRequest;
import com.autotrader.backend.exception.UnauthorizedConversationAccessException;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.ConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConversationService conversationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void createConversation_withValidRequest_returnsOkResponse() throws Exception {

        // Arrange: the controller returns ResponseEntity.ok(...) here, not
        // .created(...), so 200 is the correct expectation even though this
        // endpoint creates a resource.
        String requestBody = """
                {
                    "listingId": 1
                }
                """;

        ConversationResponse response = new ConversationResponse();
        response.setId(10L);
        response.setListingId(1L);

        when(conversationService.getOrCreateConversation(1L))
                .thenReturn(response);

        mockMvc.perform(post("/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.listingId").value(1));

        verify(conversationService).getOrCreateConversation(1L);
    }

    @Test
    void createConversation_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // Arrange: listingId is missing, which violates @NotNull.
        String requestBody = "{}";

        mockMvc.perform(post("/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verify(conversationService, never()).getOrCreateConversation(any());
    }

    @Test
    void getMyConversations_returnsOkResponseWithPagedConversations() throws Exception {

        ConversationResponse response = new ConversationResponse();
        response.setId(10L);

        Page<ConversationResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(conversationService.getCurrentUserConversations(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/conversations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10));

        verify(conversationService).getCurrentUserConversations(any(Pageable.class));
    }

    @Test
    void getConversation_whenParticipant_returnsOkResponse() throws Exception {

        ConversationResponse response = new ConversationResponse();
        response.setId(10L);

        when(conversationService.getConversation(10L)).thenReturn(response);

        mockMvc.perform(get("/conversations/{conversationId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

        verify(conversationService).getConversation(10L);
    }

    @Test
    void getConversation_whenUnauthorized_propagatesException() {

        when(conversationService.getConversation(10L))
                .thenThrow(new UnauthorizedConversationAccessException(
                        "You are not allowed to access this conversation"));

        /*
         * GlobalExceptionHandler currently has no @ExceptionHandler for
         * UnauthorizedConversationAccessException, so this exception is not
         * converted into a structured 403 response — it surfaces as an
         * unhandled exception during dispatch instead. This test documents
         * that current gap rather than a desired outcome; adding a handler
         * would let this become a normal status().isForbidden() assertion.
         */
        assertThrows(
                Exception.class,
                () -> mockMvc.perform(get("/conversations/{conversationId}", 10L))
        );
    }
}