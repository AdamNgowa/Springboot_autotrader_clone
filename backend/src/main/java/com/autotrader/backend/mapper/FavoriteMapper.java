package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.favorite.FavoriteResponse;
import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;
import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    private final VehicleListingMapper vehicleListingMapper;

    public FavoriteMapper(VehicleListingMapper vehicleListingMapper) {
        this.vehicleListingMapper = vehicleListingMapper;
    }

    public FavoriteResponse toResponse(Favorite favorite) {

        FavoriteResponse response = new FavoriteResponse();

        response.setId(favorite.getId());

        VehicleListing listing = favorite.getVehicleListing();

        VehicleListingResponse listingResponse =
                vehicleListingMapper.toResponse(listing);

        response.setListing(listingResponse);

        return response;
    }
}