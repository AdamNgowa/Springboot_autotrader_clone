package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component registers this mapper as a Spring managed-bean
//Although it doesn't contain business logic other classes depend on it
//to translate between entities and DTOs


// The mapper's responsibility is to convert an internal model into an API model
@Component
public class VehicleListingMapper {
    // Converts a vehicleListing entity into a response DTO suitable for API clients
    //Creates a new response i.e. new VehicleListingResponse ,
    // then it populates it with data with getters from vehicle listing entity
    // This process amounts to converting an entity to a request
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

    //Converts a vehicleImage entity into a imageResponse DTO
    private ImageResponse toImageResponse(VehicleImage image) {
        ImageResponse response = new ImageResponse();

        response.setId(image.getId());
        response.setImageUrl("/uploads/"+image.getStorageFilename());
        response.setPrimaryImage(image.isPrimaryImage());
        response.setDisplayOrder(image.getDisplayOrder());

        return response;
    }

    //Converts a collection of VehicleImage entities into image response DTOs
    private List<ImageResponse> toImageResponses(List<VehicleImage> images) {
        return images.stream()
                .map(this::toImageResponse)
                .toList();

    }

    //Converts create listing dto into a vehicle listing entity
    //Creates a new vehicle listing entity and populates it with response data with getters
    //from create listing request
    //This process amounts to converting a request to an entity
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
