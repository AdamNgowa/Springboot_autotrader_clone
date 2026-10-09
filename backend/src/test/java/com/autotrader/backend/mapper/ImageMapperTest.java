package com.autotrader.backend.mapper;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


public class ImageMapperTest {
    private FileStorageService fileStorageService;
    private ImageMapper imageMapper;

    @BeforeEach
    void setUp(){
        fileStorageService = mock(FileStorageService.class);
        imageMapper = new ImageMapper(fileStorageService);
    }

    @Test
    void toResponse_usesAbsoluteUrlReturnedByStorageProvider(){
        String storageFilename = "abc-123.jpg";
        String cloudinaryUrl  =
                "https://res.cloudinary.com/demo/image/upload/abc-123.jpg";

        VehicleImage image = new VehicleImage();

        image.setStorageFilename(storageFilename);
        image.setPrimaryImage(true);
        image.setDisplayOrder(0);

        when(fileStorageService.getFileUrl(storageFilename))
                .thenReturn(cloudinaryUrl);

        ImageResponse response = imageMapper.toResponse(image);

        assertThat(response.getImageUrl()).isEqualTo(cloudinaryUrl);
        assertThat(response.isPrimaryImage()).isTrue();
        assertThat(response.getDisplayOrder()).isZero();

        verify(fileStorageService).getFileUrl(storageFilename);



    }
}
