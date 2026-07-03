package com.autotrader.backend.service;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
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

@Service
public class VehicleListingService {
    public UserRepository userRepository;
    public VehicleListingRepository vehicleListingRepository;

    public VehicleListingService(
            UserRepository userRepository,
            VehicleListingRepository vehicleListingRepository) {
        this.userRepository = userRepository;
        this.vehicleListingRepository = vehicleListingRepository;
    }

    private VehicleListingResponse toResponse(
            VehicleListing listing) {

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

    public VehicleListingResponse createListing(CreateListingRequest request) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));

        //2.Convert DTO -> entity
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

        //IMPORTANT: Server controls relationships
        listing.setSeller(seller);

        // status is NOT from request → controlled by backend
        // createdAt handled by @PrePersist

        VehicleListing saved = vehicleListingRepository.save(listing);

        return toResponse(saved);

    }


    public Page<VehicleListingResponse> getListings(
            VehicleListingSearchCriteria filter,
            Pageable pageable) {

        Specification<VehicleListing> spec =
                VehicleListingSpecificationBuilder.build(filter)
                        .and(VehicleListingSpecification.hasStatus(
                                ListingStatus.ACTIVE
                        ));


        Page<VehicleListing> listings =
                vehicleListingRepository.findAll(spec, pageable);

        return listings.map(this::toResponse);

    }


}
