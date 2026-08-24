package com.autotrader.backend.controller;

import com.autotrader.backend.dto.user.SellerResponse;
import com.autotrader.backend.dto.user.UserResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.mapper.UserMapper;
import com.autotrader.backend.service.CurrentUserService;
import com.autotrader.backend.service.UserService;

import com.autotrader.backend.service.VehicleListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(
        name = "Users",
        description = "Endpoints for authenticated user information and public seller profiles"
)
public class UserController {

    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final VehicleListingService vehicleListingService;

    public UserController(
            CurrentUserService currentUserService,
            UserMapper userMapper,
            UserService userService,
            VehicleListingService vehicleListingService
            ) {

        this.currentUserService = currentUserService;
        this.userMapper = userMapper;
        this.userService = userService;
        this.vehicleListingService = vehicleListingService;
    }

    @Operation(
            summary = "Get the authenticated user",
            description = """
                    Returns the profile information of the currently
                    authenticated user based on the supplied JWT.
                    """
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = currentUserService.getAuthenticatedUser();

        UserResponse response = userMapper.toResponse(user);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a public seller profile",
            description = """
                    Returns the public profile information of a seller.
                    Sensitive account information such as email, password,
                    and role is not exposed.
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<SellerResponse> getSellerProfile(
            @PathVariable Long id) {

        User seller = userService.getUserById(id);

        SellerResponse response =
                userMapper.toSellerResponse(seller);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a seller's active listings",
            description = """
                Returns a paginated list of active vehicle listings
                belonging to the specified seller.
                """
    )

    @GetMapping("/{id}/listings")
    public ResponseEntity<Page<VehicleListingResponse>> getSellerListings(
            @PathVariable Long id,
            Pageable pageable) {

        User seller = userService.getUserById(id);

        Page<VehicleListingResponse> listings =
                vehicleListingService.getSellerActiveListings(
                        seller,
                        pageable);

        return ResponseEntity.ok(listings);
    }
}