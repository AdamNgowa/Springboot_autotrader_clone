package com.autotrader.backend.service;

import com.autotrader.backend.dto.CreateListingRequest;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.repository.VehicleListingRepository;
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

    public VehicleListing createListing(CreateListingRequest request) {
        //1.Fetch seller: Ownership validation starts here
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new RuntimeException("Seller Not Found!"));

        //2.Convert DTO -> entity
        VehicleListing listing = new VehicleListing();

        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
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

       return vehicleListingRepository.save(listing);

    }

}
