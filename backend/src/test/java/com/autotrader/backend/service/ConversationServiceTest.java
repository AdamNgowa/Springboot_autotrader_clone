package com.autotrader.backend.service;

import com.autotrader.backend.dto.messaging.ConversationResponse;
import com.autotrader.backend.entity.Conversation;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.UnauthorizedConversationAccessException;
import com.autotrader.backend.mapper.ConversationMapper;
import com.autotrader.backend.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    /*
     * ConversationRepository is mocked because this is a unit test.
     * We want to test ConversationService's decisions without touching
     * a real database.
     */
    @Mock
    private ConversationRepository conversationRepository;

    /*
     * CurrentUserService is mocked so we can control exactly which
     * User is treated as "logged in" for each test.
     */
    @Mock
    private CurrentUserService currentUserService;

    /*
     * VehicleListingService is mocked because looking up/validating
     * a listing is not what this unit test is responsible for verifying.
     */
    @Mock
    private VehicleListingService vehicleListingService;

    /*
     * ConversationMapper is mocked because entity → DTO conversion is
     * not part of what we're testing here.
     */
    @Mock
    private ConversationMapper conversationMapper;

    /*
     * Mockito creates the real ConversationService and injects the four
     * mocked dependencies into its constructor.
     */
    @InjectMocks
    private ConversationService conversationService;

    /*
     * Tests creation of a new buyer/seller conversation.
     */
    @Test
    void shouldCreateConversationSuccessfully() {

        Long listingId = 1L;

        //The user who is "logged in" and starting the conversation
        User buyer = new User();

        /*
         * User has no public setId(...) — the ID is @GeneratedValue'd by
         * the database, so there's deliberately no setter on the entity.
         * ReflectionTestUtils.setField(...) reaches into the private
         * "id" field directly, which is fine for a test since we're not
         * pretending this is something application code should ever do.
         */
        ReflectionTestUtils.setField(buyer, "id", 10L);

        //The listing owner — must be a different user than the buyer
        User seller = new User();
        ReflectionTestUtils.setField(seller, "id", 20L);

        VehicleListing listing =
                new VehicleListing();

        listing.setSeller(seller);

        //The entity that conversationRepository.save(...) will pretend to return
        Conversation conversation =
                new Conversation();

        //The DTO that conversationMapper.toResponse(...) will pretend to return
        ConversationResponse response =
                new ConversationResponse();

        //Mock execution of currentUserService.getAuthenticatedUser() and pretend it returns buyer
        when(currentUserService.getAuthenticatedUser())
                .thenReturn(buyer);

        //Mock execution of vehicleListingService.getActiveListing(...) and pretend it returns listing
        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        //Mock execution of the lookup and pretend no existing conversation was found,
        //so the service is forced down the "create a new one" path
        when(conversationRepository.findByBuyerAndSellerAndListing(
                buyer,
                seller,
                listing
        )).thenReturn(Optional.empty());

        /*
         * any(Conversation.class) is used because the actual Conversation
         * object is built inside the service — we don't have a reference
         * to it here, so we match "any" instance of that type.
         */
        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(conversation);

        when(conversationMapper.toResponse(conversation))
                .thenReturn(response);

        //Run the actual method under test
        ConversationResponse result =
                conversationService.getOrCreateConversation(listingId);

        //assertSame checks result is the EXACT same object as response,
        //confirming the service returned the mapper's output directly
        assertSame(response, result);

        verify(conversationRepository)
                .findByBuyerAndSellerAndListing(
                        buyer,
                        seller,
                        listing
                );

        verify(conversationRepository)
                .save(any(Conversation.class));

        verify(conversationMapper)
                .toResponse(conversation);
    }

    /*
     * Tests returning an existing conversation instead of creating
     * a duplicate one.
     */
    @Test
    void shouldReturnExistingConversation() {

        Long listingId = 1L;

        User buyer = new User();
        ReflectionTestUtils.setField(buyer, "id", 10L);

        User seller = new User();
        ReflectionTestUtils.setField(seller, "id", 20L);

        VehicleListing listing =
                new VehicleListing();

        listing.setSeller(seller);

        //A conversation that already exists in the "database" for this buyer/seller/listing combo
        Conversation existingConversation =
                new Conversation();

        ConversationResponse response =
                new ConversationResponse();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(buyer);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        //This time the lookup finds something, so the service should skip creation entirely
        when(conversationRepository.findByBuyerAndSellerAndListing(
                buyer,
                seller,
                listing
        )).thenReturn(Optional.of(existingConversation));

        when(conversationMapper.toResponse(existingConversation))
                .thenReturn(response);

        ConversationResponse result =
                conversationService.getOrCreateConversation(listingId);

        assertSame(response, result);

        verify(conversationRepository)
                .findByBuyerAndSellerAndListing(
                        buyer,
                        seller,
                        listing
                );

        /*
         * No save should happen because the conversation already exists.
         *
         * We don't have to call verifyNoMoreInteractions here because
         * we are specifically interested in the important business
         * interaction: no duplicate conversation should be created.
         */
        verify(conversationMapper)
                .toResponse(existingConversation);
    }

    /*
     * Tests the security rule preventing a seller from starting a
     * conversation with themselves.
     */
    @Test
    void shouldRejectConversationWithSelf() {

        Long listingId = 1L;

        //Same user plays both roles: the person making the request AND the listing's seller
        User currentUser = new User();
        ReflectionTestUtils.setField(currentUser, "id", 10L);

        VehicleListing listing =
                new VehicleListing();

        listing.setSeller(currentUser);

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        //conversationService.getOrCreateConversation(...) must throw UnauthorizedConversationAccessException.
        //It's a fail if no exception is thrown, or if a different exception type is thrown.
        assertThrows(
                UnauthorizedConversationAccessException.class,
                () -> conversationService.getOrCreateConversation(
                        listingId
                )
        );

        /*
         * The repository should never be queried because the business
         * rule failed before conversation lookup.
         */
        verifyNoInteractions(conversationRepository);
        verifyNoInteractions(conversationMapper);
    }

    /*
     * Tests retrieval of conversations belonging to the current user.
     */
    @Test
    void shouldGetCurrentUserConversations() {

        User currentUser = new User();

        //Pageable describes which "page" of results we're asking for (page 0, size 10)
        Pageable pageable =
                PageRequest.of(0, 10);

        Conversation conversation =
                new Conversation();

        ConversationResponse response =
                new ConversationResponse();

        //PageImpl simulates a real Spring Data Page containing one conversation,
        //so the service has something realistic to call .map(...) on
        Page<Conversation> page =
                new PageImpl<>(
                        List.of(conversation),
                        pageable,
                        1
                );

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        //Note: currentUser is passed twice because a conversation could have
        //the user as either the buyer OR the seller
        when(conversationRepository.findByBuyerOrSeller(
                currentUser,
                currentUser,
                pageable
        )).thenReturn(page);

        when(conversationMapper.toResponse(conversation))
                .thenReturn(response);

        Page<ConversationResponse> result =
                conversationService.getCurrentUserConversations(
                        pageable
                );

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));

        verify(conversationRepository)
                .findByBuyerOrSeller(
                        currentUser,
                        currentUser,
                        pageable
                );
    }

    /*
     * Tests successful retrieval of a specific conversation by a
     * participant.
     */
    @Test
    void shouldGetConversationForCurrentUser() {

        Long conversationId = 1L;

        User currentUser = new User();

        Conversation conversation =
                new Conversation();

        ConversationResponse response =
                new ConversationResponse();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        //Mock execution of findByIdAndParticipant and pretend the user IS a participant
        when(conversationRepository.findByIdAndParticipant(
                conversationId,
                currentUser
        )).thenReturn(Optional.of(conversation));

        when(conversationMapper.toResponse(conversation))
                .thenReturn(response);

        ConversationResponse result =
                conversationService.getConversation(
                        conversationId
                );

        assertSame(response, result);

        verify(conversationRepository)
                .findByIdAndParticipant(
                        conversationId,
                        currentUser
                );
    }

    /*
     * Tests the authorization boundary.
     *
     * If the repository cannot find a conversation for this user,
     * the service deliberately returns the same authorization-style
     * exception rather than exposing the conversation.
     */
    @Test
    void shouldRejectUnauthorizedConversationAccess() {

        Long conversationId = 1L;

        User currentUser = new User();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        //This time the lookup finds nothing — either the conversation
        //doesn't exist, or this user isn't a participant in it
        when(conversationRepository.findByIdAndParticipant(
                conversationId,
                currentUser
        )).thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedConversationAccessException.class,
                () -> conversationService.getConversation(
                        conversationId
                )
        );

        //Since the lookup failed, the mapper should never be asked
        //to convert a conversation that doesn't exist for this user
        verifyNoInteractions(conversationMapper);
    }
}