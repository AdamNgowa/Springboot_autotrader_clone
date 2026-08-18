package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.entity.VehicleImage;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageResponse toResponse(VehicleImage image) {

        ImageResponse response = new ImageResponse();

        response.setId(image.getId());
        response.setImageUrl(
                "/uploads/" + image.getStorageFilename()
        );
        response.setPrimaryImage(image.isPrimaryImage());
        response.setDisplayOrder(image.getDisplayOrder());

        return response;
    }
}