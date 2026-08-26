package com.autotrader.backend.dto.messaging;

import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {

    @NotNull(message = "Listing ID is required")
    private Long listingId;

    public CreateConversationRequest() {
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }
}