package com.autotrader.backend.controller;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.image.ReorderImagesRequest;
import com.autotrader.backend.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller providing operations for uploading, deleting, and organizing
 * vehicle listing images. Requires JWT Bearer authentication.
 */
@RestController
@RequestMapping("/listings/{listingId}/images")
@Tag(
        name = "Vehicle Images",
        description = "Operations for uploading and managing listing images"
)
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ImageService imageService;

    // Constructor injection for the image service layer dependency.
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Uploads an image file associated with a vehicle listing.
     *
     * @param listingId Dynamic path variable matching the vehicle listing identifier.
     * @param file Multi-part file payload from the request under the form key 'file'.
     * @return HTTP 200 OK containing the created ImageResponse payload.
     */

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
    public ResponseEntity<ImageResponse> uploadImage(
            @PathVariable Long listingId,
            @RequestParam("file") MultipartFile file
    ) {
        // Delegate image upload processing and return the response metadata object
        ImageResponse uploadedImage = imageService.uploadImage(listingId, file);

        return ResponseEntity.ok(uploadedImage);
    }

    /**
     * Removes an image from a listing and triggers automatic sequence adjustments.
     *
     * @param listingId Unique ID of the vehicle listing.
     * @param imageId Unique ID of the target image to remove.
     * @return HTTP 204 No Content response upon successful deletion.
     */
    @Operation(
            summary = "Delete an image",
            description = """
                    Deletes an image from a vehicle listing.
                    Only the owner may perform this operation.
                    Remaining images are automatically reordered.
                    """
    )

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long listingId,
            @PathVariable Long imageId
    ) {
        // Delegate deletion logic to service layer
        imageService.deleteImage(listingId, imageId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Sets a target image as the primary cover photo for the listing.
     *
     * @param listingId Unique ID of the vehicle listing.
     * @param imageId Unique ID of the image to designate as primary.
     * @return HTTP 200 OK containing the updated ImageResponse.
     */
    @Operation(
            summary = "Set primary image",
            description = """
                    Makes the selected image the primary image.
                    The primary image is moved to display order zero.
                    """
    )
    @PatchMapping("/{imageId}/primary")
    public ResponseEntity<ImageResponse> setPrimaryImage(
            @PathVariable Long listingId,
            @PathVariable Long imageId
    ) {
        // Delegate primary image status updates to service layer
        ImageResponse updatedImage = imageService.setPrimaryImage(listingId, imageId);

        return ResponseEntity.ok(updatedImage);
    }

    /**
     * Bulk updates the display order of all images in a vehicle listing.
     *
     * @param listingId Unique ID of the vehicle listing.
     * @param request Validated payload carrying ordered image identifiers.
     * @return HTTP 200 OK containing the full list of updated ImageResponse objects.
     */
    @Operation(
            summary = "Reorder listing images",
            description = """
                    Reorders all images belonging to a listing.
                    The first image becomes the primary image.
                    """
    )
    @PutMapping("/order")
    public ResponseEntity<List<ImageResponse>> reorderImages(
            @PathVariable Long listingId,
            @Valid @RequestBody ReorderImagesRequest request
    ) {
        // Delegate reordering logic to service layer
        List<ImageResponse> images = imageService.reorderImages(listingId, request);

        return ResponseEntity.ok(images);
    }
}