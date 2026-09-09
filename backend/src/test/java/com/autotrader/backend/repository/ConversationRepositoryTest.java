package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.*;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConversationRepositoryTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Test
    void shouldFindConversationByBuyerSellerAndListing() {
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

        // Act
        Optional<Conversation> result =
                conversationRepository
                        .findByBuyerAndSellerAndListing(
                                buyer,
                                seller,
                                listing
                        );

        // Assert
        assertThat(result).isPresent();

        assertThat(result.get().getBuyer().getId())
                .isEqualTo(buyer.getId());

        assertThat(result.get().getSeller().getId())
                .isEqualTo(seller.getId());

        assertThat(result.get().getListing().getId())
                .isEqualTo(listing.getId());
    }

    @Test
    void shouldReturnEmptyWhenConversationDoesNotExist() {
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

        // Act
        Optional<Conversation> result =
                conversationRepository
                        .findByBuyerAndSellerAndListing(
                                buyer,
                                seller,
                                listing
                        );

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindConversationsWhereUserIsBuyerOrSeller() {
        // Arrange
        User buyer = createUser(
                "buyer@example.com",
                UserRole.USER
        );

        User seller = createUser(
                "seller@example.com",
                UserRole.ADMIN
        );

        User unrelatedUser = createUser(
                "unrelated@example.com",
                UserRole.USER
        );

        VehicleListing firstListing =
                createListing(seller, "Toyota Corolla");

        VehicleListing secondListing =
                createListing(seller, "Honda Civic");

        VehicleListing unrelatedListing =
                createListing(unrelatedUser, "Mazda CX-5");

        Conversation buyerConversation =
                createConversation(
                        buyer,
                        seller,
                        firstListing
                );

        Conversation sellerConversation =
                createConversation(
                        unrelatedUser,
                        seller,
                        secondListing
                );

        Conversation unrelatedConversation =
                createConversation(
                        unrelatedUser,
                        buyer,
                        unrelatedListing
                );

        conversationRepository.saveAll(
                List.of(
                        buyerConversation,
                        sellerConversation,
                        unrelatedConversation
                )
        );

        PageRequest pageable =
                PageRequest.of(0, 10);

        // Act
        Page<Conversation> result =
                conversationRepository.findByBuyerOrSeller(
                        buyer,
                        seller,
                        pageable
                );

        // Assert
        assertThat(result.getTotalElements())
                .isEqualTo(2);

        assertThat(result.getContent())
                .hasSize(2);

        assertThat(result.getContent())
                .extracting(Conversation::getListing)
                .extracting(VehicleListing::getTitle)
                .containsExactlyInAnyOrder(
                        "Toyota Corolla",
                        "Honda Civic"
                );
    }

    @Test
    void shouldFindConversationWhenUserIsParticipant() {
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

        // Act
        Optional<Conversation> result =
                conversationRepository.findByIdAndParticipant(
                        conversation.getId(),
                        buyer
                );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId())
                .isEqualTo(conversation.getId());
    }

    @Test
    void shouldReturnEmptyWhenUserIsNotParticipant() {
        // Arrange
        User buyer = createUser(
                "buyer@example.com",
                UserRole.USER
        );

        User seller = createUser(
                "seller@example.com",
                UserRole.ADMIN
        );

        User unrelatedUser = createUser(
                "unrelated@example.com",
                UserRole.USER
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

        // Act
        Optional<Conversation> result =
                conversationRepository.findByIdAndParticipant(
                        conversation.getId(),
                        unrelatedUser
                );

        // Assert
        assertThat(result).isEmpty();
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
}