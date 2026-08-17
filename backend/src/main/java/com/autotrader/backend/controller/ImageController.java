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
    // Maps HTTP POST requests to this method and restricts acceptance
    // strictly to multipart/form-data content (matching the FormData sent from JS).
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    // Declares a public handler returning ResponseEntity<Void>,
    // indicating an HTTP response with no response body payload.
    public ResponseEntity<Void> uploadImage(
            // Extracts the dynamic {listingId} segment from the URL path
            // (e.g., /listings/42/images) and converts it to a Java Long.
            @PathVariable Long listingId,

            // Extracts the binary payload sent under the key "file"
            // (from formData.append("file", file)) and wraps it into a Spring MultipartFile.
            @RequestParam("file") MultipartFile file
    ) {

        // Delegates core logic (authorization, file validation, S3/disk saving,
        // and database updates) to the service layer.
        imageService.uploadImage(listingId, file);

        // Constructs and returns an HTTP 204 No Content response to signal
        // successful processing without sending unnecessary data back.
        return ResponseEntity.noContent().build();
    }

}