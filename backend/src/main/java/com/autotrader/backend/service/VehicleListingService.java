package com.autotrader.backend.service;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.AuthenticatedUserNotFoundException;
import com.autotrader.backend.exception.ListingNotFoundException;
import com.autotrader.backend.exception.UnauthorizedListingAccessException;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.repository.VehicleListingRepository;
import com.autotrader.backend.specification.VehicleListingSpecification;
import com.autotrader.backend.specification.VehicleListingSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

// @Service tags this class as a Spring-managed Bean handling core vehicle listing business logic.
@Service
public class VehicleListingService {

    // 1. DECLARING PERMANENT SLOTS (DEPENDENCIES)
    private final UserRepository userRepository;
    private final VehicleListingRepository vehicleListingRepository;

    // 2. CONSTRUCTOR INJECTION
    public VehicleListingService(
            UserRepository userRepository,
            VehicleListingRepository vehicleListingRepository) {
        this.userRepository = userRepository;
        this.vehicleListingRepository = vehicleListingRepository;
    }

    // ==========================================
    // MAPPING/CONVERSION UTILITY
    // ==========================================

    // Private mapper method to translate a Database Entity into a clean Data Transfer Object (DTO)
    private VehicleListingResponse toResponse(VehicleListing listing) {
        return new VehicleListingResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getMake(),
                listing.getModel(),
                listing.getYear(),
                listing.getCity()
        );
    }

    // ==========================================
    // CORE BUSINESS OPERATIONS
    // ==========================================

    // Handles saving a new vehicle listing submitted by an authenticated user
    public VehicleListingResponse createListing(CreateListingRequest request) {
        // Fetch the complete authenticated User record using our helper method
        User seller = getAuthenticatedUser();

        // Convert Request DTO -> Database Entity model
        VehicleListing listing = new VehicleListing();
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setYear(request.getYear());
        listing.setMake(request.getMake());
        listing.setModel(request.getModel());
        listing.setMileage(request.getMileage());
        listing.setFuelType(request.getFuelType());
        listing.setTransmission(request.getTransmission());
        listing.setBodyType(request.getBodyType());
        listing.setCity(request.getCity());

        // Connect the current user directly to the listing as the designated owner/seller
        listing.setSeller(seller);

        // Commit the populated listing object safely into the database tables
        VehicleListing saved = vehicleListingRepository.save(listing);

        // Map the saved entity back into a clean payload response and return it
        return toResponse(saved);
    }

    // Fetches a filtered, dynamic, and paginated list of vehicle listings
    public Page<VehicleListingResponse> getListings(
            VehicleListingSearchCriteria filter,
            Pageable pageable) {

        // Combine search conditions dynamically using JPA Specifications, ensuring we ONLY fetch ACTIVE listings
        Specification<VehicleListing> spec =
                VehicleListingSpecificationBuilder.build(filter)
                        .and(VehicleListingSpecification.hasStatus(
                                ListingStatus.ACTIVE
                        ));

        // Query the database passing both search filters and pagination rules
        Page<VehicleListing> listings =
                vehicleListingRepository.findAll(spec, pageable);

        // Map every entity inside the page results into a clean DTO output structure
        return listings.map(this::toResponse);
    }

    // Handles overwriting properties on an active vehicle listing
    public VehicleListingResponse updateListing(
            Long listingId,
            UpdateListingRequest request
    ){
        // 1. Fetch the listing via helper (automatically handles validation and soft-deletes check)
        VehicleListing listing = getActiveListing(listingId);

        // 2. Fetch the authenticated context user via helper
        User authenticatedUser = getAuthenticatedUser();

        // 3. Verify security ownership credentials via helper
        verifyOwnership(listing, authenticatedUser);

        // 4. Overwrite the editable fields with fresh request data
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setYear(request.getYear());
        listing.setMake(request.getMake());
        listing.setModel(request.getModel());
        listing.setMileage(request.getMileage());
        listing.setFuelType(request.getFuelType());
        listing.setTransmission(request.getTransmission());
        listing.setBodyType(request.getBodyType());
        listing.setCity(request.getCity());

        // Persist modifications back to database
        VehicleListing updatedListing = vehicleListingRepository.save(listing);

        return toResponse(updatedListing);
    }

    // Performs a safe logical soft-delete on an active listing
    public void deleteListing(Long listingId) {
        // 1. Fetch the active listing via helper
        VehicleListing listing = getActiveListing(listingId);

        // 2. Fetch the current user via helper
        User authenticatedUser = getAuthenticatedUser();

        // 3. Verify the caller owns the listing before deletion processing
        verifyOwnership(listing, authenticatedUser);

        // 4. Toggle the listing status to DELETED instead of wiping the raw database record
        listing.setStatus(ListingStatus.DELETED);

        // Save status change state to database
        vehicleListingRepository.save(listing);
    }

    // Fetches a single specific active vehicle listing package
    public VehicleListingResponse getListingById(Long listingId){
        // Fetch the active listing via helper and parse directly into response structure
        VehicleListing listing = getActiveListing(listingId);
        return toResponse(listing);
    }

    // ==========================================
    // PRIVATE HELPER METHODS (Reusable Subroutines)
    // ==========================================

    // Reach into the Spring Security context to grab and load the active authenticated user profile
    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()->
                        new AuthenticatedUserNotFoundException("Authenticated user not found"));
    }

    // Queries a database listing record by ID and throws an error if it doesn't exist or is soft-deleted
    private VehicleListing getActiveListing(Long listingId) {
        VehicleListing listing = vehicleListingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ListingNotFoundException("Listing not found"));

        if (listing.getStatus() == ListingStatus.DELETED) {
            throw new ListingNotFoundException("Listing not found");
        }

        return listing;
    }

    // Halts processing and throws an unauthorized exception if the context user doesn't own the targeting listing
    private void verifyOwnership(VehicleListing listing, User authenticatedUser) {
        if (!listing.getSeller().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedListingAccessException(
                    "You are not allowed to modify this listing");
        }
    }
}