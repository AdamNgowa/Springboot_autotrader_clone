package com.autotrader.backend.dto.favorite;

import com.autotrader.backend.dto.vehicleListing.VehicleListingResponse;

public class FavoriteResponse {

    private Long id;
    private VehicleListingResponse listing;

    public FavoriteResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleListingResponse getListing() {
        return listing;
    }

    public void setListing(VehicleListingResponse listing) {
        this.listing = listing;
    }
}