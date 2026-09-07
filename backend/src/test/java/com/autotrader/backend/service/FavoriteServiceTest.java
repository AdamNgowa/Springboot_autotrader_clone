package com.autotrader.backend.service;

import com.autotrader.backend.dto.favorite.FavoriteResponse;
import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.mapper.FavoriteMapper;
import com.autotrader.backend.repository.FavoriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    /*
     * FavoriteRepository is mocked because this is a unit test.
     * We want to test FavoriteService's decisions without touching
     * a real database.
     */
    @Mock
    private FavoriteRepository favoriteRepository;

    /*
     * CurrentUserService is mocked so we can control exactly which
     * User is treated as "logged in" for each test.
     */
    @Mock
    private CurrentUserService currentUserService;

    /*
     * VehicleListingService is mocked because listing lookup/validation
     * is not what this unit test is responsible for verifying.
     */
    @Mock
    private VehicleListingService vehicleListingService;

    /*
     * FavoriteMapper is mocked because entity → DTO conversion is not
     * part of what we're testing here.
     */
    @Mock
    private FavoriteMapper favoriteMapper;

    /*
     * Mockito creates the real FavoriteService and injects the four
     * mocked dependencies into its constructor.
     */
    @InjectMocks
    private FavoriteService favoriteService;

    /*
     * Tests successful addition of a favorite.
     *
     * Expected flow:
     *
     * authenticated user
     *       ↓
     * active listing retrieved
     *       ↓
     * favorite does not already exist
     *       ↓
     * Favorite entity created
     *       ↓
     * repository.save(...)
     */
    @Test
    void shouldAddFavoriteSuccessfully() {

        Long listingId = 1L;

        User user = new User();

        VehicleListing listing =
                new VehicleListing();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        //Pretend this user has NOT already favorited this listing,
        //so the service should proceed to create one
        when(favoriteRepository.existsByUserAndVehicleListing(
                user,
                listing
        )).thenReturn(false);

        favoriteService.addFavorite(listingId);

        /*
         * We don't know the Favorite object before the service creates it,
         * so we verify that save(...) was called with any Favorite instance.
         */
        verify(favoriteRepository).save(
                org.mockito.ArgumentMatchers.any(Favorite.class)
        );

        verify(currentUserService)
                .getAuthenticatedUser();

        verify(vehicleListingService)
                .getActiveListing(listingId);

        verify(favoriteRepository)
                .existsByUserAndVehicleListing(user, listing);
    }

    /*
     * Tests duplicate prevention.
     *
     * A user should not receive multiple Favorite rows for the same
     * listing.
     */
    @Test
    void shouldNotCreateDuplicateFavorite() {

        Long listingId = 1L;

        User user = new User();

        VehicleListing listing =
                new VehicleListing();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        //This time pretend the favorite already exists
        when(favoriteRepository.existsByUserAndVehicleListing(
                user,
                listing
        )).thenReturn(true);

        favoriteService.addFavorite(listingId);

        /*
         * The existence check is true, so the service should return
         * without saving anything.
         */
        verify(favoriteRepository)
                .existsByUserAndVehicleListing(user, listing);

        //Confirms save(...) (or anything else) was never called on the
        //repository beyond the existence check we already verified above
        verifyNoMoreInteractions(favoriteRepository);
    }

    /*
     * Tests favorite removal.
     */
    @Test
    void shouldRemoveFavorite() {

        Long listingId = 1L;

        User user = new User();

        VehicleListing listing =
                new VehicleListing();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        favoriteService.removeFavorite(listingId);

        verify(favoriteRepository)
                .deleteByUserAndVehicleListing(user, listing);
    }

    /*
     * Tests checking whether a listing is favorited.
     */
    @Test
    void shouldReturnTrueWhenListingIsFavorite() {

        Long listingId = 1L;

        User user = new User();

        VehicleListing listing =
                new VehicleListing();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(vehicleListingService.getActiveListing(listingId))
                .thenReturn(listing);

        when(favoriteRepository.existsByUserAndVehicleListing(
                user,
                listing
        )).thenReturn(true);

        boolean result =
                favoriteService.isFavorite(listingId);

        assertEquals(true, result);

        verify(favoriteRepository)
                .existsByUserAndVehicleListing(user, listing);
    }

    /*
     * Tests retrieving the authenticated user's favorites.
     */
    @Test
    void shouldGetCurrentUserFavorites() {

        User user = new User();

        Favorite favorite =
                new Favorite();

        FavoriteResponse response =
                new FavoriteResponse();

        when(currentUserService.getAuthenticatedUser())
                .thenReturn(user);

        when(favoriteRepository.findByUser(user))
                .thenReturn(List.of(favorite));

        when(favoriteMapper.toResponse(favorite))
                .thenReturn(response);

        List<FavoriteResponse> result =
                favoriteService.getCurrentUserFavorites();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        verify(favoriteRepository)
                .findByUser(user);

        verify(favoriteMapper)
                .toResponse(favorite);
    }
}