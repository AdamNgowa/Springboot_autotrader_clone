package com.autotrader.backend.controller;

import com.autotrader.backend.dto.favorite.FavoriteResponse;
import com.autotrader.backend.dto.favorite.FavoriteStatusResponse;
import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@Tag(
        name = "Favorites",
        description = "Operations for managing the authenticated user's favorite listings"
)
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(
            summary = "Add a listing to favorites",
            description = """
                    Adds an active vehicle listing to the authenticated user's favorites.
                    The authenticated user is determined from the JWT.
                    """
    )
    @PostMapping("/{listingId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long listingId
    ) {

        favoriteService.addFavorite(listingId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove a listing from favorites",
            description = """
                    Removes a vehicle listing from the authenticated user's favorites.
                    """
    )
    @DeleteMapping("/{listingId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long listingId
    ) {

        favoriteService.removeFavorite(listingId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Retrieve the authenticated user's favorites",
            description = """
                    Returns the favorite relationships belonging to the authenticated user.
                    """
    )
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getCurrentUserFavorites() {

        List<FavoriteResponse> favorites =
                favoriteService.getCurrentUserFavorites();

        return ResponseEntity.ok(favorites);
    }

    @Operation(
            summary = "Check favorite status",
            description = """
                Checks whether the authenticated user has favorited
                the specified vehicle listing.
                """
    )
    @GetMapping("/{listingId}/status")
    public ResponseEntity<FavoriteStatusResponse> getFavoriteStatus(
            @PathVariable Long listingId
    ) {

        boolean favorite =
                favoriteService.isFavorite(listingId);

        return ResponseEntity.ok(
                new FavoriteStatusResponse(favorite)
        );
    }
}