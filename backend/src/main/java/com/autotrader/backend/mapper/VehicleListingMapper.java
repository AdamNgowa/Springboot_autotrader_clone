package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.stereotype.Component;

//@Component registers this mapper as a Spring managed-bean
//Although it doesn't contain business logic other classes depend on it
//to translate between entities and DTOs
@Component
public class VehicleListingMapper {
    // Converts a vehicleListing entity into a response DTO suitable for API clients
    public VehicleListingResponse toResponse(VehicleListing listing) {
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

    //Converts create listing dto into a vehicle listing entity
    public VehicleListing toEntity(CreateListingRequest request){



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

        return listing;

    }

    // Copies editable fields from an UpdateListingRequest into an existing VehicleListing entity.
    public void updateEntity(
            UpdateListingRequest request,
            VehicleListing listing) {

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
    }

}
