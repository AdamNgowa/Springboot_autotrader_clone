package com.autotrader.backend.service;

import com.autotrader.backend.dto.favorite.FavoriteResponse;
import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.mapper.FavoriteMapper;
import com.autotrader.backend.repository.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CurrentUserService currentUserService;
    private final VehicleListingService vehicleListingService;
    private final FavoriteMapper favoriteMapper;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            CurrentUserService currentUserService,
            VehicleListingService vehicleListingService,
            FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.currentUserService = currentUserService;
        this.vehicleListingService = vehicleListingService;
        this.favoriteMapper = favoriteMapper;
    }

    /**
     * Standard read-write transaction.
     * Opens an active database transaction that commits upon completion or rolls back on runtime exceptions.
     *
     * Ensures atomicity across multiple queries: user authentication, active listing validation,
     * duplicate check, and entity save all execute inside a single unified persistence context.
     */
    @Transactional
    public void addFavorite(Long listingId) {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        VehicleListing listing =
                vehicleListingService.getActiveListing(listingId);

        boolean alreadyFavorited =
                favoriteRepository.existsByUserAndVehicleListing(
                        authenticatedUser,
                        listing
                );

        if (alreadyFavorited) {
            return;
        }

        Favorite favorite = new Favorite();

        favorite.setUser(authenticatedUser);
        favorite.setVehicleListing(listing);

        // Saved within the current transaction; modifications are committed to DB when method completes
        favoriteRepository.save(favorite);
    }

    /**
     * Standard read-write transaction.
     * Ensures atomic removal of the favorite entity from the database.
     *
     * Keeps the JPA Session open during record deletion so foreign key checks
     * and cascading rules execute cleanly within a single transaction boundaries.
     */
    @Transactional
    public void removeFavorite(Long listingId) {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        VehicleListing listing =
                vehicleListingService.getActiveListing(listingId);

        favoriteRepository.deleteByUserAndVehicleListing(
                authenticatedUser,
                listing
        );
    }

    /**
     * Read-only transaction optimization.
     * Explicitly disables Hibernate dirty-checking and snapshot creation to conserve memory and CPU.
     *
     * Keeps the DB session open during entity lookup without lock overhead, returning a boolean cleanly.
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(Long listingId) {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        VehicleListing listing =
                vehicleListingService.getActiveListing(listingId);

        return favoriteRepository.existsByUserAndVehicleListing(
                authenticatedUser,
                listing
        );
    }

    /**
     * Read-only transaction optimization.
     * Keeps the JPA persistence context open throughout method execution.
     *
     * Prevents LazyInitializationException if child/associated entities on the returned
     * Favorite objects (e.g., favorite.getVehicleListing()) are accessed later in service layers.
     */
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getCurrentUserFavorites() {

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        List<Favorite> favorites =
                favoriteRepository.findByUser(authenticatedUser);

        return favorites.stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }
}