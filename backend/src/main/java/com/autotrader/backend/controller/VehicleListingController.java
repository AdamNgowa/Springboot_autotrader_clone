package com.autotrader.backend.controller;

import com.autotrader.backend.dto.CreateListingRequest;
import com.autotrader.backend.dto.VehicleListingResponse;
import com.autotrader.backend.dto.VehicleListingSearchCriteria;
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
     * * ResponseEntity<VehicleListingResponse>: A Spring wrapper class allowing full control over
     * the HTTP response (Status Codes & Headers). The body will contain a VehicleListing Response DTO.
     * * @RequestBody: Automatically converts (deserializes) the incoming JSON payload from
     * the frontend request into an instance of CreateListingRequest DTO.
     */
    @PostMapping
    public ResponseEntity<VehicleListingResponse> createListing(@RequestBody CreateListingRequest request) {

        /*
         * 2. PROCESSING THE DATA
         * * Passes the DTO request object into the service layer. The service handles the
         * business logic (finding the seller, mapping relationships, and executing repository calls).
         * It returns the fully populated, database-saved VehicleListing entity mapped to a response DTO.
         */
        VehicleListingResponse savedListing = vehicleListingService.createListing(request);

        /*
         * 3. BUILDING THE PERFECT REST RESPONSE
         * * Uses a fluent builder pattern to return a professional RESTful response:
         * * .created(...): Sets the HTTP Status Code to '201 Created', the formal standard for saved resources.
         * URI.create(...): Adds a 'Location' header pointing to the new resource URI (e.g., /listings/45).
         * .body(savedListing): Serializes and injects the saved response DTO object as a JSON response body.
         */
        return ResponseEntity
                .created(URI.create("/listings/" + savedListing.getId()))
                .body(savedListing);
    }

    /**
     * 1. CATCHING THE QUERY AND PAGINATION PARAMETERS
     * * @GetMapping: Handles incoming HTTP GET requests sent to the "/listings" pathway. Used for fetching data.
     * * ResponseEntity<Page<VehicleListingResponse>>: Returns an HTTP response containing a Spring 'Page' object wrapper.
     * This wrapper houses the list of vehicle DTOs alongside pagination metadata (total elements, total pages, etc.).
     * * @ModelAttribute: Maps standard URL Query parameters (e.g., ?city=Nairobi&make=Toyota) directly into the fields
     * of the VehicleListingSearchCriteria DTO. Missing parameters remain null, making filters fully optional.
     * * Pageable: Automatically binds page control query parameters (e.g., ?page=0&size=20&sort=price,asc) from the URL
     * into a Spring Data configuration object.
     */
    @GetMapping
    public ResponseEntity<Page<VehicleListingResponse>> getListings(
            @ModelAttribute VehicleListingSearchCriteria filter, Pageable pageable) {

        /*
         * 2. PROCESSING THE FILTERED SEARCH
         * * Forwards the extracted search criteria DTO and pagination configuration down to the service layer.
         * * The service works with JPA Specifications to build a dynamic SQL query based on what fields were filled,
         * executes the query efficiently using pagination limits, and converts the matching database entities into DTOs.
         */
        Page<VehicleListingResponse> listings = vehicleListingService.getListings(filter, pageable);

        /*
         * 3. BUILDING THE PERFECT REST RESPONSE
         * * Leverages the fluent response builder to reply with an standard fetch confirmation:
         * * .ok(...): Sets the HTTP Status Code to '200 OK', confirming the server successfully processed the request.
         * * .body(listings): Embeds the paginated catalog results metadata and data arrays into the HTTP JSON response body.
         */
        return ResponseEntity.ok(listings);
    }

}