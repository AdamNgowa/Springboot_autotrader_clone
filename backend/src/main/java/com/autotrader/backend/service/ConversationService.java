package com.autotrader.backend.service;

import com.autotrader.backend.dto.messaging.ConversationResponse;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.UnauthorizedConversationAccessException;
import com.autotrader.backend.mapper.ConversationMapper;
import com.autotrader.backend.repository.ConversationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final CurrentUserService currentUserService;
    private final VehicleListingService vehicleListingService;
    private final ConversationMapper conversationMapper;


    public ConversationService(
            ConversationRepository conversationRepository,
            CurrentUserService currentUserService,
            VehicleListingService vehicleListingService,
            ConversationMapper conversationMapper
    ) {
        this.conversationRepository = conversationRepository;
        this.currentUserService = currentUserService;
        this.vehicleListingService = vehicleListingService;
        this.conversationMapper = conversationMapper;
    }

    public ConversationResponse getOrCreateConversation(Long listingId) {

        User buyer = currentUserService.getAuthenticatedUser();

        VehicleListing listing =
                vehicleListingService.getActiveListing(listingId);

        User seller = listing.getSeller();

        if (seller.getId().equals(buyer.getId())) {
            throw new UnauthorizedConversationAccessException(
                    "You cannot start a conversation with yourself"
            );
        }

        Conversation conversation =
                conversationRepository
                        .findByBuyerAndSellerAndListing(
                                buyer,
                                seller,
                                listing
                        )
                        .orElseGet(() -> createConversation(
                                buyer,
                                seller,
                                listing
                        ));

        return conversationMapper.toResponse(conversation);
    }    private Conversation createConversation(
            User buyer,
            User seller,
            VehicleListing listing
    ) {

        Conversation conversation = new Conversation();

        conversation.setBuyer(buyer);
        conversation.setSeller(seller);
        conversation.setListing(listing);

        return conversationRepository.save(conversation);
    }

    public Page<ConversationResponse> getCurrentUserConversations(
            Pageable pageable
    ) {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        Page<Conversation> conversations =
                conversationRepository.findByBuyerOrSeller(
                        authenticatedUser,
                        authenticatedUser,
                        pageable
                );

        return conversations.map(conversationMapper::toResponse);
    }

    public ConversationResponse getConversation(Long conversationId) {

        Conversation conversation =
                getConversationForCurrentUser(conversationId);

        return conversationMapper.toResponse(conversation);
    }

    public Conversation getConversationForCurrentUser(
            Long conversationId
    ) {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        return conversationRepository
                .findByIdAndParticipant(
                        conversationId,
                        authenticatedUser
                )
                .orElseThrow(() ->
                        new UnauthorizedConversationAccessException(
                                "You are not allowed to access this conversation"
                        )
                );
    }
}