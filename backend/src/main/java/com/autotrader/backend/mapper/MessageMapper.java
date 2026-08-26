package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.messaging.MessageResponse;
import com.autotrader.backend.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message) {

        MessageResponse response = new MessageResponse();

        response.setId(message.getId());

        response.setConversationId(
                message.getConversation().getId()
        );

        response.setSenderId(
                message.getSender().getId()
        );

        response.setSenderFirstName(
                message.getSender().getFirstName()
        );

        response.setSenderLastName(
                message.getSender().getLastName()
        );

        response.setContent(
                message.getContent()
        );

        response.setCreatedAt(
                message.getCreatedAt()
        );

        return response;
    }
}