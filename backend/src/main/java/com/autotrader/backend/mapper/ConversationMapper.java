package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.messaging.ConversationResponse;
import com.autotrader.backend.entity.Conversation;
import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

    public ConversationResponse toResponse(Conversation conversation) {

        ConversationResponse response = new ConversationResponse();

        response.setId(conversation.getId());

        response.setListingId(
                conversation.getListing().getId()
        );

        response.setListingTitle(
                conversation.getListing().getTitle()
        );

        response.setBuyerId(
                conversation.getBuyer().getId()
        );

        response.setBuyerFirstName(
                conversation.getBuyer().getFirstName()
        );

        response.setBuyerLastName(
                conversation.getBuyer().getLastName()
        );

        response.setSellerId(
                conversation.getSeller().getId()
        );

        response.setSellerFirstName(
                conversation.getSeller().getFirstName()
        );

        response.setSellerLastName(
                conversation.getSeller().getLastName()
        );

        response.setCreatedAt(
                conversation.getCreatedAt()
        );

        return response;
    }
}