package com.autotrader.backend.service;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
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
    // Note: Changing these to private final is best practice to keep them permanent and immutable.
    private final UserRepository userRepository;
    private final VehicleListingRepository vehicleListingRepository;

    // 2. CONSTRUCTOR INJECTION
    // Spring automatically wires the database access repositories into our slots.
    public VehicleListingService(
            UserRepository userRepository,
            VehicleListingRepository vehicleListingRepository) {
        this.userRepository = userRepository;
        this.vehicleListingRepository = vehicleListingRepository;
    }

    // ==========================================
    // MAPPING/CONVERSION UTILITY
    // ==========================================

    // Private mapper method to translate a raw Database Entity (VehicleListing)
    // into a clean, customized Data Transfer Object (VehicleListingResponse) sent to the client frontend.
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

        // Step 1: Reach into Spring Security's central vault to grab the current user's login passport
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Step 2: Extract the unique identifier (the user's email) from the security passport
        String email = authentication.getName();

        // Step 3: Fetch the complete User record from the database using that email string
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        // Step 4: Convert Request DTO -> Database Entity model
        // We create a fresh, blank database row object and explicitly populate it from the request packet fields
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

        // Step 5: Establish the Database Relationship
        // Connect our fetched 'User' object directly into the listing as the designated owner/seller
        listing.setSeller(seller);

        // NOTE ON BACKEND OVERRIDES:
        // ListingStatus (e.g., ACTIVE/PENDING) is intentionally ignored from the user request;
        // the backend business rules explicitly control statuses. Timestamps are managed by database hooks like @PrePersist.

        // Step 6: Commit the populated listing object safely into the database tables
        VehicleListing saved = vehicleListingRepository.save(listing);

        // Step 7: Map the saved entity back into a clean payload response and return it
        return toResponse(saved);
    }

    // Fetches a filtered, dynamic, and paginated list of vehicle listings
    public Page<VehicleListingResponse> getListings(
            VehicleListingSearchCriteria filter, // Contains custom search items (e.g., make, model, maxPrice)
            Pageable pageable) {                 // Holds pagination rules (page number, page size, sort order)

        // Step 1: Combine search conditions dynamically using JPA Specifications
        // It reads the user criteria fields to build standard SQL queries,
        // and chains an '.and()' condition ensuring we ONLY display vehicles that are actively on sale.
        Specification<VehicleListing> spec =
                VehicleListingSpecificationBuilder.build(filter)
                        .and(VehicleListingSpecification.hasStatus(
                                ListingStatus.ACTIVE
                        ));

        // Step 2: Query the database passing both our advanced search filters (spec) and pagination metadata (pageable)
        // This stops your database from crashing by loading exactly what is needed (e.g., 20 items per page).
        Page<VehicleListing> listings =
                vehicleListingRepository.findAll(spec, pageable);

        // Step 3: Smoothly loop through the fetched Database Page elements and use the method reference (this::toResponse)
        // to map every single database item into a clean DTO output structure before returning it.
        return listings.map(this::toResponse);
    }

    public VehicleListingResponse updateListing(
            Long ListingId,
            UpdateListingRequest request
    ){
        //1.Find the vehicle listing being updated
        VehicleListing listing = vehicleListingRepository.findById(ListingId)
                .orElseThrow(()->
                        new ListingNotFoundException("Listing not found"));

        //2.Get the authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        //3.Load the authenticated user from the database
        User authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(()->
                        new RuntimeException("Authenticated user not found"));

        //4.Verify ownership
        if(!listing.getSeller().getId().equals(authenticatedUser.getId())){
            throw new UnauthorizedListingAccessException("You are not allowed to updated this listing");
        }

        //5. update the editable fields
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

        VehicleListing updatedListing = vehicleListingRepository.save(listing);

        return toResponse(updatedListing);

    }

    public void deleteListing(Long listingId){
        //1.Find the listing
        VehicleListing listing = vehicleListingRepository.findById(listingId)
                .orElseThrow(()->
                        new ListingNotFoundException("Listing not found"));
        //2.Get the authenticated user's email
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        //3.Load the authenticated user
        User authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(()->
                        new RuntimeException("Authenticated User not found"));


        //4. Verify ownership
        if(!listing.getSeller().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedListingAccessException("You are not allowed to delete this listing");
        }

        //4.Delete the listing
        vehicleListingRepository.delete(listing);
    }

    public VehicleListingResponse getListingById(Long listingId){
        VehicleListing listing = vehicleListingRepository.findById(listingId)
                .orElseThrow(()->
                        new ListingNotFoundException("Listing not found"));

        if (listing.getStatus() == ListingStatus.DELETED){
            throw new ListingNotFoundException("Listing not found");
        }

        return toResponse(listing);
    }

}