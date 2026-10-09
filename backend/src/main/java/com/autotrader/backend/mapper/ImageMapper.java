package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.service.FileStorageService;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    private final FileStorageService fileStorageService;

    public ImageMapper(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public ImageResponse toResponse(VehicleImage image) {

        ImageResponse response = new ImageResponse();

        response.setId(image.getId());
        response.setImageUrl(
               fileStorageService.getFileUrl(image.getStorageFilename())
        );
        response.setPrimaryImage(image.isPrimaryImage());
        response.setDisplayOrder(image.getDisplayOrder());

        return response;
    }
}