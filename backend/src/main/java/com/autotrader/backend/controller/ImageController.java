package com.autotrader.backend.controller;

import com.autotrader.backend.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/listings/{listingId}/images")
@Tag(
        name = "Vehicle Images",
        description = "Operations for uploading and managing listing images"
)
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(
            summary = "Upload an image",
            description = """
                    Uploads an image for a vehicle listing.
                    Only the owner of the listing may upload images.
                    Supported image formats are JPEG, PNG and WEBP.
                    """
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadImage(
            @PathVariable Long listingId,
            @RequestParam("file") MultipartFile file
    ) {

        imageService.uploadImage(listingId, file);

        return ResponseEntity.noContent().build();
    }
}