package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.*;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.Message;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Test
    void shouldFindMessagesForConversationOrderedByCreatedAtAscending() {
        // Arrange
        User buyer = createUser(
                "buyer@example.com",
                UserRole.USER
        );

        User seller = createUser(
                "seller@example.com",
                UserRole.ADMIN
        );

        VehicleListing listing =
                createListing(seller, "Toyota Corolla");

        Conversation conversation =
                createConversation(
                        buyer,
                        seller,
                        listing
                );

        conversationRepository.save(conversation);

        Message firstMessage =
                createMessage(
                        conversation,
                        buyer,
                        "Hello, is this vehicle still available?"
                );

        Message secondMessage =
                createMessage(
                        conversation,
                        seller,
                        "Yes, it is still available."
                );

        Message thirdMessage =
                createMessage(
                        conversation,
                        buyer,
                        "Can I come view it tomorrow?"
                );

        messageRepository.save(firstMessage);
        messageRepository.save(secondMessage);
        messageRepository.save(thirdMessage);

        PageRequest pageable =
                PageRequest.of(0, 10);

        // Act
        Page<Message> result =
                messageRepository
                        .findByConversationOrderByCreatedAtAscIdAsc(
                                conversation,
                                pageable
                        );

        // Assert
        assertThat(result.getTotalElements())
                .isEqualTo(3);

        assertThat(result.getContent())
                .hasSize(3);

        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly(
                        "Hello, is this vehicle still available?",
                        "Yes, it is still available.",
                        "Can I come view it tomorrow?"
                );
    }

    @Test
    void shouldOnlyReturnMessagesFromRequestedConversation() {
        // Arrange
        User firstBuyer = createUser(
                "firstbuyer@example.com",
                UserRole.USER
        );

        User secondBuyer = createUser(
                "secondbuyer@example.com",
                UserRole.USER
        );

        User seller = createUser(
                "seller@example.com",
                UserRole.ADMIN
        );

        VehicleListing firstListing =
                createListing(seller, "Toyota Corolla");

        VehicleListing secondListing =
                createListing(seller, "Honda Civic");

        Conversation firstConversation =
                createConversation(
                        firstBuyer,
                        seller,
                        firstListing
                );

        Conversation secondConversation =
                createConversation(
                        secondBuyer,
                        seller,
                        secondListing
                );

        conversationRepository.saveAll(
                List.of(
                        firstConversation,
                        secondConversation
                )
        );

        Message firstConversationMessage =
                createMessage(
                        firstConversation,
                        firstBuyer,
                        "Message in first conversation"
                );

        Message secondConversationMessage =
                createMessage(
                        secondConversation,
                        secondBuyer,
                        "Message in second conversation"
                );

        messageRepository.saveAll(
                List.of(
                        firstConversationMessage,
                        secondConversationMessage
                )
        );

        PageRequest pageable =
                PageRequest.of(0, 10);

        // Act
        Page<Message> result =
                messageRepository
                        .findByConversationOrderByCreatedAtAscIdAsc(
                                firstConversation,
                                pageable
                        );

        // Assert
        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent().get(0).getContent())
                .isEqualTo("Message in first conversation");
    }

    @Test
    void shouldSupportPaginationForConversationMessages() {
        // Arrange
        User buyer = createUser(
                "buyer@example.com",
                UserRole.USER
        );

        User seller = createUser(
                "seller@example.com",
                UserRole.ADMIN
        );

        VehicleListing listing =
                createListing(seller, "Toyota Corolla");

        Conversation conversation =
                createConversation(
                        buyer,
                        seller,
                        listing
                );

        conversationRepository.save(conversation);

        Message firstMessage =
                createMessage(
                        conversation,
                        buyer,
                        "First message"
                );

        Message secondMessage =
                createMessage(
                        conversation,
                        seller,
                        "Second message"
                );

        Message thirdMessage =
                createMessage(
                        conversation,
                        buyer,
                        "Third message"
                );

        messageRepository.saveAll(
                List.of(
                        firstMessage,
                        secondMessage,
                        thirdMessage
                )
        );

        PageRequest pageable =
                PageRequest.of(0, 2);

        // Act
        Page<Message> result =
                messageRepository
                        .findByConversationOrderByCreatedAtAscIdAsc(
                                conversation,
                                pageable
                        );

        // Assert
        assertThat(result.getContent())
                .hasSize(2);

        assertThat(result.getTotalElements())
                .isEqualTo(3);

        assertThat(result.getTotalPages())
                .isEqualTo(2);

        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly(
                        "First message",
                        "Second message"
                );
    }

    private User createUser(
            String email,
            UserRole role
    ) {
        User user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setPhoneNumber("0712345678");
        user.setRole(role);

        return userRepository.save(user);
    }

    private VehicleListing createListing(
            User seller,
            String title
    ) {
        VehicleListing listing = new VehicleListing();

        listing.setTitle(title);
        listing.setDescription("Test description");
        listing.setPrice(new BigDecimal("15000.00"));
        listing.setYear(2022);
        listing.setMake("Toyota");
        listing.setModel("Corolla");
        listing.setMileage(25000);
        listing.setFuelType(FuelType.PETROL);
        listing.setTransmission(Transmission.AUTOMATIC);
        listing.setBodyType(BodyType.SEDAN);
        listing.setCity("Nairobi");
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setSeller(seller);

        return vehicleListingRepository.save(listing);
    }

    private Conversation createConversation(
            User buyer,
            User seller,
            VehicleListing listing
    ) {
        Conversation conversation = new Conversation();

        conversation.setBuyer(buyer);
        conversation.setSeller(seller);
        conversation.setListing(listing);

        return conversation;
    }

    private Message createMessage(
            Conversation conversation,
            User sender,
            String content
    ) {
        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);

        return message;
    }
}