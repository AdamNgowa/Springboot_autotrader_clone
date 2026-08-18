package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.user.SellerResponse;
import com.autotrader.backend.dto.vehicleListing.CreateListingRequest;
import com.autotrader.backend.dto.vehicleListing.UpdateListingRequest;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.stereotype.Component;

import java.util.Comparator;
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
        VehicleListingResponse response = new VehicleListingResponse();

        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());

        response.setPrice(listing.getPrice());

        response.setMake(listing.getMake());
        response.setModel(listing.getModel());
        response.setYear(listing.getYear());
        response.setMileage(listing.getMileage());

        response.setFuelType(listing.getFuelType());
        response.setTransmission(listing.getTransmission());
        response.setBodyType(listing.getBodyType());

        response.setCity(listing.getCity());

        response.setSeller(
                toSellerResponse(listing.getSeller())
        );


        response.setImages(
                toImageResponses(listing.getImages())
        );

        return response;

    }

    // Converts a listing seller into the seller information exposed to marketplace clients
    private SellerResponse toSellerResponse(User seller) {
        return new SellerResponse(
                seller.getId(),
                seller.getFirstName(),
                seller.getLastName(),
                seller.getPhoneNumber()
        );
    }

    //Converts a vehicleImage entity into a imageResponse DTO
    private ImageResponse toImageResponse(VehicleImage image) {
        ImageResponse response = new ImageResponse();

        response.setId(image.getId());
        response.setImageUrl("/uploads/" + image.getStorageFilename());
        response.setPrimaryImage(image.isPrimaryImage());
        response.setDisplayOrder(image.getDisplayOrder());

        return response;
    }

    // Converts a collection of VehicleImage entities into image response DTOs, sorted by display order
    private List<ImageResponse> toImageResponses(
            List<VehicleImage> images
    ) {
        // 1. .stream() converts the 'images' collection into a sequential Stream (conveyor belt)
        return images.stream()
                // 2. .sorted() reorders the stream items before passing them down the pipeline
                .sorted(
                        // Comparator.comparing extracts the property we want to sort by
                        Comparator.comparing(
                                // Extract the 'displayOrder' value from each VehicleImage
                                VehicleImage::getDisplayOrder,
                                // Safe comparison handler that prevents NullPointerExceptions
                                Comparator.nullsLast(
                                        // Sort non-null display orders in ascending numerical order (1, 2, 3...)
                                        Comparator.naturalOrder()
                                )
                        )
                )
                // 3. .map() transforms each now-sorted VehicleImage entity into an ImageResponse DTO
                .map(this::toImageResponse)
                // 4. .toList() collects the transformed, ordered ImageResponse DTOs into an unmodifiable List
                .toList();
    }

    //Converts create listing dto into a vehicle listing entity
    //Creates a new vehicle listing entity and populates it with response data with getters
    //from create listing request
    //This process amounts to converting a request to an entity
    public VehicleListing toEntity(CreateListingRequest request) {


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
