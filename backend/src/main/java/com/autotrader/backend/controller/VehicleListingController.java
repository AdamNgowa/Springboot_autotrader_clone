package com.autotrader.backend.controller;

import com.autotrader.backend.dto.CreateListingRequest;
import com.autotrader.backend.dto.VehicleListingResponse;
import com.autotrader.backend.dto.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.service.VehicleListingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/listings")
public class VehicleListingController {

    private final VehicleListingService vehicleListingService;


    public VehicleListingController(VehicleListingService vehicleListingService) {
        this.vehicleListingService = vehicleListingService;

    }

    /**
     * 1. CATCHING THE REQUEST
     * * @PostMapping: Handles incoming HTTP POST requests sent to the "/listings" pathway.
     * * ResponseEntity<VehicleListing>: A Spring wrapper class allowing full control over
     * the HTTP response (Status Codes & Headers). The body will contain a VehicleListing object.
     * * @RequestBody: Automatically converts (deserializes) the incoming JSON payload from
     * the frontend request into an instance of CreateListingRequest DTO.
     */
    @PostMapping
    public ResponseEntity<VehicleListingResponse> createListing(@RequestBody CreateListingRequest request) {

        /*
         * 2. PROCESSING THE DATA
         * * Passes the DTO request object into the service layer. The service handles the
         * business logic (finding the seller, mapping relationships, and executing repository calls).
         * It returns the fully populated, database-saved VehicleListing entity with its generated ID.
         */
        VehicleListingResponse savedListing = vehicleListingService.createListing(request);

        /*
         * 3. BUILDING THE PERFECT REST RESPONSE
         * * Uses a fluent builder pattern to return a professional RESTful response:
         * * .created(...): Sets the HTTP Status Code to '201 Created', the formal standard for saved resources.
         * URI.create(...): Adds a 'Location' header pointing to the new resource URI (e.g., /listings/45).
         * .body(savedListing): Serializes and injects the saved entity object as a JSON response body.
         */
        return ResponseEntity
                .created(URI.create("/listings/" + savedListing.getId()))
                .body(savedListing);
    }

    @GetMapping
    public ResponseEntity<Page<VehicleListingResponse>> getListings(
            @ModelAttribute VehicleListingSearchCriteria filter, Pageable pageable) {
        Page<VehicleListingResponse> listings = vehicleListingService.getListings(filter, pageable);
        return ResponseEntity.ok(listings);
    }

}