package com.autotrader.backend.controller;

import com.autotrader.backend.dto.messaging.CreateMessageRequest;
import com.autotrader.backend.dto.messaging.MessageResponse;
import com.autotrader.backend.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
@Tag(
        name = "Messages",
        description = "Operations for sending and retrieving conversation messages"
)
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;

    public MessageController(
            MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Retrieve conversation messages",
            description = """
                    Returns messages belonging to the specified conversation.

                    The authenticated user must be a participant
                    in the conversation.
                    """
    )
    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            Pageable pageable
    ) {

        Page<MessageResponse> messages =
                messageService.getMessages(
                        conversationId,
                        pageable
                );

        return ResponseEntity.ok(messages);
    }

    @Operation(
            summary = "Send a message",
            description = """
                    Sends a message to a conversation.

                    The authenticated user becomes the sender.
                    The sender ID must not be supplied by the client.

                    The authenticated user must be a participant
                    in the conversation.
                    """
    )
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,

            @Valid
            @RequestBody CreateMessageRequest request
    ) {

        MessageResponse message =
                messageService.sendMessage(
                        conversationId,
                        request
                );

        return ResponseEntity.ok(message);
    }
}