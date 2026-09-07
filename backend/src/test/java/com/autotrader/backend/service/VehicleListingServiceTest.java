package com.autotrader.backend.service;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.ListingNotFoundException;
import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.exception.UnauthorizedListingAccessException;
import com.autotrader.backend.mapper.VehicleListingMapper;
import com.autotrader.backend.repository.VehicleListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/*
 * Unit tests for VehicleListingService.
 *
 * VehicleListingService coordinates several responsibilities:
 *
 * 1. CurrentUserService
 *    Determines which application User is making the request.
 *
 * 2. VehicleListingRepository
 *    Reads and writes VehicleListing data.
 *
 * 3. VehicleListingMapper
 *    Converts between entities and DTOs.
 *
 * Because this is a unit test, all three collaborators are mocked.
 *
 * We are testing the business decisions made by VehicleListingService,
 * not JPA, the database, or the mapper implementation itself.
 */
@ExtendWith(MockitoExtension.class)
class VehicleListingServiceTest {

    @Mock
    private VehicleListingRepository vehicleListingRepository;

    @Mock
    private VehicleListingMapper vehicleListingMapper;

    @Mock
    private CurrentUserService currentUserService;

    /*
     * Mockito creates the real VehicleListingService and injects the
     * three mocked dependencies into its constructor.
     */
    @InjectMocks
    private VehicleListingService vehicleListingService;

    /*
     * Tests successful listing creation.
     *
     * Expected flow:
     *
     * authenticated user
     *       ↓
     * map request → entity
     *       ↓
     * assign authenticated user as seller
     *       ↓
     * save entity
     *       ↓
     * map entity → response
     */
    @Test
    void shouldCreateListingSuccessfully() {

        User currentUser = new User();

        //The DTO that would normally arrive from the controller
        CreateListingRequest request =
                new CreateListingRequest();

        //The entity that vehicleListingMapper.toEntity(...) will pretend to return
        VehicleListing listing =
                new VehicleListing();

        VehicleListingResponse expectedResponse =
                new VehicleListingResponse();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        when(vehicleListingMapper.toEntity(request))
                .thenReturn(listing);

        //save(listing) returns the SAME object reference, mirroring how
        //JPA's save() typically returns the entity you passed in
        when(vehicleListingRepository.save(listing))
                .thenReturn(listing);

        when(vehicleListingMapper.toResponse(listing))
                .thenReturn(expectedResponse);

        VehicleListingResponse actualResponse =
                vehicleListingService.createListing(request);

        assertSame(expectedResponse, actualResponse);

        /*
         * The seller must be the currently authenticated user.
         *
         * This is a business rule, so we verify the actual entity
         * that was passed to the repository, by checking the field
         * the service is expected to have set on it.
         */
        assertSame(currentUser, listing.getSeller());

        verify(currentUserService).getAuthenticatedUser();
        verify(vehicleListingMapper).toEntity(request);
        verify(vehicleListingRepository).save(listing);
        verify(vehicleListingMapper).toResponse(listing);
    }

    /*
     * Tests retrieval of active listings.
     *
     * The service should:
     *
     * 1. Build the filtering Specification.
     * 2. Force the ListingStatus to ACTIVE.
     * 3. Ask the repository for a Page.
     * 4. Convert every entity into a response DTO.
     */
    @Test
    void shouldGetActiveListings() {

        Pageable pageable =
                PageRequest.of(0, 10);

        /*
         * An empty search criteria object, as if the user applied no filters.
         * We use a real instance here (not null) because the real service
         * builds a Specification from this object's fields — passing null
         * crashes VehicleListingSpecificationBuilder, which only handles
         * "no filters set", not "no object at all". A real controller always
         * constructs this object, even with every field left unset, so this
         * matches how the method is actually called in production.
         */
        VehicleListingSearchCriteria emptyCriteria =
                new VehicleListingSearchCriteria();

        //PageImpl simulates a real Spring Data Page containing two listings
        Page<VehicleListing> listingPage =
                new PageImpl<>(
                        List.of(
                                new VehicleListing(),
                                new VehicleListing()
                        ),
                        pageable,
                        2
                );

        when(vehicleListingRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(listingPage);

        VehicleListingResponse firstResponse =
                new VehicleListingResponse();

        VehicleListingResponse secondResponse =
                new VehicleListingResponse();

        when(vehicleListingMapper.toResponse(any(VehicleListing.class)))
                .thenReturn(firstResponse, secondResponse);

        Page<VehicleListingResponse> result =
                vehicleListingService.getListings(
                        emptyCriteria,
                        pageable
                );

        assertEquals(2, result.getTotalElements());
        assertEquals(firstResponse, result.getContent().get(0));
        assertEquals(secondResponse, result.getContent().get(1));

        /*
         * We don't inspect the Specification internals here.
         *
         * That is the responsibility of repository/specification tests.
         *
         * Here we only care that the service delegates to the repository
         * with a Specification and the requested Pageable.
         */
        verify(vehicleListingRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    /*
     * Tests retrieval of the authenticated user's own listings.
     */
    @Test
    void shouldGetCurrentUserListings() {

        User currentUser = new User();

        Pageable pageable =
                PageRequest.of(0, 10);

        VehicleListing listing =
                new VehicleListing();

        VehicleListingResponse response =
                new VehicleListingResponse();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        when(vehicleListingRepository.findBySellerAndStatus(
                currentUser,
                ListingStatus.ACTIVE,
                pageable
        )).thenReturn(
                new PageImpl<>(
                        List.of(listing),
                        pageable,
                        1
                )
        );

        when(vehicleListingMapper.toResponse(listing))
                .thenReturn(response);

        Page<VehicleListingResponse> result =
                vehicleListingService.getCurrentUserListings(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));

        verify(currentUserService).getAuthenticatedUser();

        verify(vehicleListingRepository)
                .findBySellerAndStatus(
                        currentUser,
                        ListingStatus.ACTIVE,
                        pageable
                );

        verify(vehicleListingMapper).toResponse(listing);
    }

    /*
     * Tests retrieval of another seller's active listings.
     *
     * This method is public marketplace functionality, so it does not
     * require the authenticated user to be the seller.
     */
    @Test
    void shouldGetSellerActiveListings() {

        User seller = new User();

        Pageable pageable =
                PageRequest.of(0, 10);

        VehicleListing listing =
                new VehicleListing();

        VehicleListingResponse response =
                new VehicleListingResponse();

        when(vehicleListingRepository.findBySellerAndStatus(
                seller,
                ListingStatus.ACTIVE,
                pageable
        )).thenReturn(
                new PageImpl<>(
                        List.of(listing),
                        pageable,
                        1
                )
        );

        when(vehicleListingMapper.toResponse(listing))
                .thenReturn(response);

        Page<VehicleListingResponse> result =
                vehicleListingService.getSellerActiveListings(
                        seller,
                        pageable
                );

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().get(0));

        verify(vehicleListingRepository)
                .findBySellerAndStatus(
                        seller,
                        ListingStatus.ACTIVE,
                        pageable
                );
    }

    /*
     * Tests successful listing update.
     *
     * Important business rule:
     *
     * Only the owner of the listing may update it.
     */
    @Test
    void shouldUpdateListingWhenCurrentUserOwnsListing() {

        Long listingId = 1L;

        User currentUser = new User();

        /*
         * User has no public setId(...) — the ID is @GeneratedValue'd, so
         * the entity deliberately doesn't expose a setter for it.
         * ReflectionTestUtils.setField(...) sets the private "id" field
         * directly, which is acceptable in tests even though it wouldn't
         * be acceptable in application code.
         */
        ReflectionTestUtils.setField(currentUser, "id", 10L);

        VehicleListing listing =
                new VehicleListing();

        //Same ID as currentUser, simulating "this user owns this listing"
        User seller = new User();
        ReflectionTestUtils.setField(seller, "id", 10L);

        listing.setSeller(seller);
        listing.setStatus(ListingStatus.ACTIVE);

        UpdateListingRequest request =
                new UpdateListingRequest();

        VehicleListingResponse response =
                new VehicleListingResponse();

        /*
         * The service's updateListing(...) internally calls its own
         * getActiveListing(...) helper, which calls repository.findById(...).
         * We mock at the repository level since that's the real dependency
         * boundary — getActiveListing itself is real code, not mocked.
         */
        when(vehicleListingRepository.findById(listingId))
                .thenReturn(Optional.of(listing));

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        when(vehicleListingRepository.save(listing))
                .thenReturn(listing);

        when(vehicleListingMapper.toResponse(listing))
                .thenReturn(response);

        VehicleListingResponse result =
                vehicleListingService.updateListing(
                        listingId,
                        request
                );

        /*
         * The mapper should update the existing entity rather than
         * creating a completely new listing.
         */
        verify(vehicleListingMapper)
                .updateEntity(request, listing);

        verify(vehicleListingRepository)
                .save(listing);

        verify(vehicleListingMapper)
                .toResponse(listing);

        assertSame(response, result);
    }

    /*
     * Tests that a user cannot update another user's listing.
     *
     * Expected flow:
     *
     * listing found
     *      ↓
     * authenticated user retrieved
     *      ↓
     * ownership check fails
     *      ↓
     * UnauthorizedListingAccessException
     *
     * The entity must not be saved.
     */
    @Test
    void shouldRejectUpdateWhenCurrentUserDoesNotOwnListing() {

        Long listingId = 1L;

        User currentUser = new User();
        ReflectionTestUtils.setField(currentUser, "id", 10L);

        //Different ID from currentUser — this user is NOT the owner
        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 20L);

        VehicleListing listing =
                new VehicleListing();

        listing.setSeller(owner);
        listing.setStatus(ListingStatus.ACTIVE);

        UpdateListingRequest request =
                new UpdateListingRequest();

        when(vehicleListingRepository.findById(listingId))
                .thenReturn(Optional.of(listing));

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        assertThrows(
                UnauthorizedListingAccessException.class,
                () -> vehicleListingService.updateListing(
                        listingId,
                        request
                )
        );

        /*
         * Because ownership failed, neither the mapper nor repository
         * should modify the listing.
         */
        verifyNoInteractions(vehicleListingMapper);

        verify(vehicleListingRepository)
                .findById(listingId);
    }

    /*
     * Tests soft deletion.
     *
     * The application does NOT physically delete the listing.
     * Instead, the status is changed to DELETED.
     */
    @Test
    void shouldSoftDeleteListingWhenCurrentUserOwnsListing() {

        Long listingId = 1L;

        User currentUser = new User();
        ReflectionTestUtils.setField(currentUser, "id", 10L);

        User owner = new User();
        ReflectionTestUtils.setField(owner, "id", 10L);

        VehicleListing listing =
                new VehicleListing();

        listing.setSeller(owner);
        listing.setStatus(ListingStatus.ACTIVE);

        when(vehicleListingRepository.findById(listingId))
                .thenReturn(Optional.of(listing));

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(currentUser);

        vehicleListingService.deleteListing(listingId);

        /*
         * This is the actual business behavior we want to protect:
         * checking the object's own state after the method ran,
         * rather than just checking that some method was called.
         */
        assertEquals(ListingStatus.DELETED, listing.getStatus());

        verify(vehicleListingRepository)
                .save(listing);
    }

    /*
     * Tests that an unknown listing cannot be updated.
     */
    @Test
    void shouldThrowExceptionWhenUpdatingMissingListing() {

        Long listingId = 999L;

        //Pretend the repository has no listing with this ID at all
        when(vehicleListingRepository.findById(listingId))
                .thenReturn(Optional.empty());

        assertThrows(
                ListingNotFoundException.class,
                () -> vehicleListingService.updateListing(
                        listingId,
                        new UpdateListingRequest()
                )
        );

        /*
         * Authentication is not needed because the listing lookup
         * already failed.
         */
        verifyNoInteractions(currentUserService);
        verifyNoInteractions(vehicleListingMapper);
    }

    /*
     * Tests that a deleted listing behaves as if it does not exist
     * for normal marketplace retrieval.
     */
    @Test
    void shouldThrowExceptionWhenGettingDeletedListing() {

        Long listingId = 1L;

        //This listing technically exists in the DB but is soft-deleted
        VehicleListing deletedListing =
                new VehicleListing();

        deletedListing.setStatus(ListingStatus.DELETED);

        when(vehicleListingRepository.findByIdWithImages(listingId))
                .thenReturn(Optional.of(deletedListing));

        assertThrows(
                ListingNotFoundException.class,
                () -> vehicleListingService.getListingById(listingId)
        );

        //Since the listing is treated as "not found", the mapper should
        //never get a chance to convert it into a response
        verifyNoInteractions(vehicleListingMapper);
    }
}