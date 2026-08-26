package com.autotrader.backend.service;

import com.autotrader.backend.dto.messaging.MessageResponse;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.Message;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.mapper.MessageMapper;
import com.autotrader.backend.repository.MessageRepository;
import com.autotrader.backend.dto.messaging.CreateMessageRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final CurrentUserService currentUserService;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    public MessageService(
            MessageRepository messageRepository,
            CurrentUserService currentUserService,
            ConversationService conversationService,
            MessageMapper messageMapper
    ) {
        this.messageRepository = messageRepository;
        this.currentUserService = currentUserService;
        this.conversationService = conversationService;
        this.messageMapper = messageMapper;
    }

    public MessageResponse sendMessage(
            Long conversationId,
            CreateMessageRequest request
    ) {

        User sender =
                currentUserService.getAuthenticatedUser();

        Conversation conversation =
                conversationService.getConversationForCurrentUser(
                        conversationId
                );

        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());

        Message savedMessage =
                messageRepository.save(message);

        return messageMapper.toResponse(savedMessage);
    }

    public Page<MessageResponse> getMessages(
            Long conversationId,
            Pageable pageable
    ) {

        Conversation conversation =
                conversationService.getConversationForCurrentUser(
                        conversationId
                );

        Page<Message> messages =
                messageRepository.findByConversationOrderByCreatedAtAsc(
                        conversation,
                        pageable
                );

        return messages.map(messageMapper::toResponse);
    }
}