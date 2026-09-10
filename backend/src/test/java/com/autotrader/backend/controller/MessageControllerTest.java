package com.autotrader.backend.controller;

import com.autotrader.backend.dto.messaging.CreateMessageRequest;
import com.autotrader.backend.dto.messaging.MessageResponse;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.MessageService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void getMessages_returnsOkResponseWithPagedMessages() throws Exception {

        MessageResponse response = new MessageResponse();
        response.setId(1L);
        response.setContent("Hello, is this still available?");

        Page<MessageResponse> page =
                new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        when(messageService.getMessages(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/conversations/{conversationId}/messages", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content")
                        .value("Hello, is this still available?"));

        verify(messageService).getMessages(eq(1L), any(Pageable.class));
    }

    @Test
    void sendMessage_withValidRequest_returnsOkResponse() throws Exception {

        String requestBody = """
                {
                    "content": "Is this vehicle still available?"
                }
                """;

        MessageResponse response = new MessageResponse();
        response.setId(2L);
        response.setContent("Is this vehicle still available?");

        when(messageService.sendMessage(eq(1L), any(CreateMessageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/conversations/{conversationId}/messages", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.content").value("Is this vehicle still available?"));

        verify(messageService).sendMessage(eq(1L), any(CreateMessageRequest.class));
    }

    @Test
    void sendMessage_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // content is blank, which violates @NotBlank on CreateMessageRequest.
        String requestBody = """
                {
                    "content": ""
                }
                """;

        mockMvc.perform(post("/conversations/{conversationId}/messages", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verify(messageService, never())
                .sendMessage(any(), any(CreateMessageRequest.class));
    }
}