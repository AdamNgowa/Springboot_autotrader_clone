package com.autotrader.backend.controller;

import com.autotrader.backend.dto.messaging.ConversationResponse;
import com.autotrader.backend.dto.messaging.CreateConversationRequest;
import com.autotrader.backend.service.ConversationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/conversations")
@Tag(
        name = "Conversations",
        description = "Operations for buyer and seller conversations"
)
@SecurityRequirement(name = "bearerAuth")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @Operation(
            summary = "Create a conversation",
            description = """
                    Creates a conversation between the authenticated user
                    and the seller of a vehicle listing.

                    The authenticated user is determined from the JWT.
                    The buyer must not supply their own user ID.
                    """
    )
    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid
            @RequestBody CreateConversationRequest request
    ) {

        ConversationResponse conversation =
                conversationService.getOrCreateConversation(
                        request.getListingId()
                );

        return ResponseEntity.ok(conversation);
    }

    @Operation(
            summary = "Retrieve current user's conversations",
            description = """
                    Returns all conversations in which the authenticated
                    user is a participant.
                    """
    )
    @GetMapping
    public ResponseEntity<Page<ConversationResponse>> getMyConversations(
            Pageable pageable
    ) {

        Page<ConversationResponse> conversations =
                conversationService.getCurrentUserConversations(
                        pageable
                );

        return ResponseEntity.ok(conversations);
    }

    @Operation(
            summary = "Retrieve a conversation",
            description = """
                    Returns a specific conversation.

                    The authenticated user must be a participant
                    in the requested conversation.
                    """
    )
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversation(
            @PathVariable Long conversationId
    ) {

        ConversationResponse conversation =
                conversationService.getConversation(
                        conversationId
                );

        return ResponseEntity.ok(conversation);
    }
}