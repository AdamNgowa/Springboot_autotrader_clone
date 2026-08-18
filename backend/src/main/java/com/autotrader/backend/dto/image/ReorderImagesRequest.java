package com.autotrader.backend.dto.image;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ReorderImagesRequest {

    @NotEmpty(message = "Image IDs must not be empty")
    private List<@NotNull Long> imageIds;

    public ReorderImagesRequest() {
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}