package com.autotrader.backend.service;

import com.autotrader.backend.dto.messaging.CreateMessageRequest;
import com.autotrader.backend.dto.messaging.MessageResponse;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.Message;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.mapper.MessageMapper;
import com.autotrader.backend.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    /*
     * MessageRepository is mocked because this is a unit test.
     * We want to test MessageService's decisions without touching
     * a real database.
     */
    @Mock
    private MessageRepository messageRepository;

    /*
     * ConversationService is mocked because MessageService delegates
     * conversation lookup/authorization to it. We are not testing
     * ConversationService's own logic here.
     */
    @Mock
    private ConversationService conversationService;

    /*
     * CurrentUserService is mocked so we can control exactly which
     * User is treated as "logged in" for each test, without needing
     * real Spring Security context.
     */
    @Mock
    private CurrentUserService currentUserService;

    /*
     * MessageMapper is mocked because entity → DTO conversion is not
     * what this unit test is responsible for verifying.
     */
    @Mock
    private MessageMapper messageMapper;

    /*
     * Mockito creates the real MessageService and injects the four
     * mocked dependencies into its constructor.
     */
    @InjectMocks
    private MessageService messageService;

    /*
     * Tests successful message sending.
     *
     * Expected flow:
     *
     * current user
     *       ↓
     * conversation authorization
     *       ↓
     * create Message
     *       ↓
     * save Message
     *       ↓
     * map Message → MessageResponse
     */
    @Test
    void shouldSendMessageSuccessfully() {

        Long conversationId = 1L;

        //The user who is "logged in" and sending the message
        User sender = new User();

        //The conversation that conversationService will pretend to authorize
        Conversation conversation =
                new Conversation();

        //The DTO the controller would normally pass in, containing message content
        CreateMessageRequest request =
                new CreateMessageRequest();

        //The entity that messageRepository.save(...) will pretend to return
        Message message =
                new Message();

        //The DTO that messageMapper.toResponse(...) will pretend to return
        MessageResponse response =
                new MessageResponse();

        //Mock execution of currentUserService.getAuthenticatedUser() and pretend it returns sender
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(sender);

        //Mock execution of conversationService.getConversationForCurrentUser(...)
        //and pretend the caller is authorized to access this conversation
        when(conversationService.getConversationForCurrentUser(
                conversationId
        )).thenReturn(conversation);

        /*
         * We don't know the exact Message object the service will build
         * internally (it's constructed inside sendMessage), so any(Message.class)
         * tells Mockito "match any Message argument" rather than a specific instance.
         */
        when(messageRepository.save(any(Message.class)))
                .thenReturn(message);

        when(messageMapper.toResponse(message))
                .thenReturn(response);

        //Run the actual method under test
        MessageResponse result =
                messageService.sendMessage(
                        conversationId,
                        request
                );

        //assertSame checks that result is the EXACT same object reference as response,
        //not just an equal-looking one. This confirms the service returned what the
        //mapper gave it, without creating a new object along the way.
        assertSame(response, result);

        verify(currentUserService)
                .getAuthenticatedUser();

        /*
         * This is a critical security interaction.
         *
         * MessageService delegates conversation authorization to
         * ConversationService rather than trusting the conversation ID.
         */
        verify(conversationService)
                .getConversationForCurrentUser(
                        conversationId
                );

        verify(messageRepository)
                .save(any(Message.class));

        verify(messageMapper)
                .toResponse(message);
    }

    /*
     * Tests retrieval of messages for an authorized conversation.
     */
    @Test
    void shouldGetMessages() {

        Long conversationId = 1L;

        User currentUser = new User();

        Conversation conversation =
                new Conversation();

        //Pageable describes which "page" of results we're asking for (page 0, size 20)
        Pageable pageable =
                PageRequest.of(0, 20);

        Message firstMessage =
                new Message();

        Message secondMessage =
                new Message();

        MessageResponse firstResponse =
                new MessageResponse();

        MessageResponse secondResponse =
                new MessageResponse();

        //PageImpl simulates a real Spring Data Page object containing our two messages,
        //so the service has something realistic to call .map(...) on
        Page<Message> page =
                new PageImpl<>(
                        List.of(
                                firstMessage,
                                secondMessage
                        ),
                        pageable,
                        2
                );

        when(conversationService.getConversationForCurrentUser(
                conversationId
        )).thenReturn(conversation);

        when(messageRepository.findByConversationOrderByCreatedAtAsc(
                conversation,
                pageable
        )).thenReturn(page);

        //Each message needs its own mocked mapping, since toResponse
        //is called once per entity in the page
        when(messageMapper.toResponse(firstMessage))
                .thenReturn(firstResponse);

        when(messageMapper.toResponse(secondMessage))
                .thenReturn(secondResponse);

        Page<MessageResponse> result =
                messageService.getMessages(
                        conversationId,
                        pageable
                );

        //Confirm the page still reports 2 total elements after mapping
        assertEquals(2, result.getTotalElements());

        //Confirm the order was preserved: first message maps to first response, etc.
        assertSame(
                firstResponse,
                result.getContent().get(0)
        );

        assertSame(
                secondResponse,
                result.getContent().get(1)
        );

        /*
         * Authorization must occur before messages are queried.
         */
        verify(conversationService)
                .getConversationForCurrentUser(
                        conversationId
                );

        verify(messageRepository)
                .findByConversationOrderByCreatedAtAsc(
                        conversation,
                        pageable
                );

        verify(messageMapper)
                .toResponse(firstMessage);

        verify(messageMapper)
                .toResponse(secondMessage);
    }

    /*
     * Tests that MessageService does not bypass conversation
     * authorization.
     *
     * If ConversationService throws an authorization exception,
     * MessageService should allow that exception to propagate.
     *
     * We don't need to manufacture the exception ourselves here.
     * Mockito can make the mocked dependency throw it.
     */
    @Test
    void shouldNotQueryMessagesWhenConversationAccessIsRejected() {

        Long conversationId = 1L;

        Pageable pageable =
                PageRequest.of(0, 20);

        //The exact exception instance we want conversationService to throw
        RuntimeException authorizationException =
                new RuntimeException(
                        "Conversation access denied"
                );

        //thenThrow tells Mockito: instead of returning a value,
        //throw this exception when the method is called
        when(conversationService.getConversationForCurrentUser(
                conversationId
        )).thenThrow(authorizationException);

        //assertThrows captures and returns the exception that was thrown,
        //so we can inspect it afterwards (unlike a plain assertThrows call
        //where we only check the type)
        RuntimeException thrown =
                org.junit.jupiter.api.Assertions.assertThrows(
                        RuntimeException.class,
                        () -> messageService.getMessages(
                                conversationId,
                                pageable
                        )
                );

        //Confirm it's literally the same exception object we set up,
        //proving the service let it propagate rather than swallowing
        //or wrapping it in something else
        assertSame(
                authorizationException,
                thrown
        );

        /*
         * Because conversation authorization failed, the message
         * repository must never be queried.
         */
        verify(conversationService)
                .getConversationForCurrentUser(
                        conversationId
                );
    }
}