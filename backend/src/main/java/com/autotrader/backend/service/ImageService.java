package com.autotrader.backend.service;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.image.ReorderImagesRequest;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.ImageNotFoundException;
import com.autotrader.backend.mapper.ImageMapper;
import com.autotrader.backend.repository.VehicleImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final CurrentUserService currentUserService;
    private final VehicleListingService vehicleListingService;
    private final VehicleImageRepository vehicleImageRepository;
    private final FileStorageService fileStorageService;
    private final ImageMapper imageMapper;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public ImageService(
            CurrentUserService currentUserService,
            VehicleListingService vehicleListingService,
            VehicleImageRepository vehicleImageRepository,
            FileStorageService fileStorageService,
            ImageMapper imageMapper
    ) {
        this.currentUserService = currentUserService;
        this.vehicleListingService = vehicleListingService;
        this.vehicleImageRepository = vehicleImageRepository;
        this.fileStorageService = fileStorageService;
        this.imageMapper = imageMapper;
    }

    // ==========================================
    // CORE BUSINESS OPERATIONS
    // ==========================================

    public ImageResponse uploadImage(
            Long listingId,
            MultipartFile file
    ) {
        validateImageFile(file);

        // Retrieve the listing and make sure it exists and has not been deleted
        VehicleListing listing =
                vehicleListingService.getActiveListing(listingId);

        // Retrieve the authenticated user
        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        // Ensure the authenticated user owns the listing
        vehicleListingService.verifyOwnership(
                listing,
                authenticatedUser
        );

        String storageFilename =
                generateStorageFilename(file);

        /*
         * Here is where we actually save the image itself.
         * Try adding the file to the file system (Could be local i.e. on disk or remote like Amazon S3).
         * If saving file to file system fails, throw an IOException wrapped in a RuntimeException.
         */
        try {
            fileStorageService.saveFile(
                    file.getInputStream(),
                    storageFilename
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read upload file",
                    e
            );
        }

        /*
         * Here we save the entity data (metadata like filenames, size, content type).
         * Try saving the image metadata; if anything goes wrong, clean up the stored file and rethrow.
         */
        try {
            long imageCount =
                    vehicleImageRepository.countByVehicleListing(
                            listing
                    );

            // If count is 0, this is the first image so set primaryImage to true
            boolean primaryImage = imageCount == 0;

            VehicleImage vehicleImage =
                    createVehicleImage(
                            listing,
                            file,
                            storageFilename,
                            primaryImage,
                            (int) imageCount
                    );

            VehicleImage savedImage =
                    vehicleImageRepository.save(vehicleImage);

            return imageMapper.toResponse(savedImage);

        } catch (Exception e) {
            // Roll back the file system storage if database insertion fails
            fileStorageService.deleteFile(storageFilename);
            throw e;
        }
    }

    /**
     * Deletes an image by removing its entity record and physical file,
     * then re-indexes remaining images to maintain sequential display order.
     */
    @Transactional
    public void deleteImage(
            Long listingId,
            Long imageId
    ) {
        System.out.println("DELETE IMAGE REQUEST");
        System.out.println("listingId = " + listingId);
        System.out.println("imageId = " + imageId);

        VehicleListing listing =
                getOwnedListingWithImages(listingId);

        VehicleImage image =
                findImageInListing(listing, imageId);

        String storageFilename =
                image.getStorageFilename();
        vehicleImageRepository.delete(image);
        vehicleImageRepository.flush();

        fileStorageService.deleteFile(storageFilename);

        normalizeImages(listing);

    }

    /**
     * Sets a specific image as primary (displayOrder = 0) and shifts all other images down.
     */
    @Transactional
    public ImageResponse setPrimaryImage(
            Long listingId,
            Long imageId
    ) {
        VehicleListing listing =
                getOwnedListingWithImages(listingId);

        List<VehicleImage> images =
                vehicleImageRepository
                        .findByVehicleListingOrderByDisplayOrderAsc(
                                listing
                        );

        // Locate the targeted image in the listing's list
        VehicleImage targetImage =
                images.stream()
                        .filter(image ->
                                image.getId().equals(imageId)
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new ImageNotFoundException(
                                        "Image not found"
                                )
                        );

        images.remove(targetImage);

        // Assign top priority to the selected image
        targetImage.setPrimaryImage(true);
        targetImage.setDisplayOrder(0);

        // Re-index remaining images starting from display order index 1
        for (int i = 0; i < images.size(); i++) {
            VehicleImage image = images.get(i);

            image.setPrimaryImage(false);
            image.setDisplayOrder(i + 1);
        }

        vehicleImageRepository.save(targetImage);
        vehicleImageRepository.saveAll(images);

        return imageMapper.toResponse(targetImage);
    }

    /**
     * Updates the custom sequence/display positions of all images belonging to a listing.
     */
    @Transactional
    public List<ImageResponse> reorderImages(
            Long listingId,
            ReorderImagesRequest request
    ) {
        VehicleListing listing =
                getOwnedListingWithImages(listingId);

        List<VehicleImage> images =
                vehicleImageRepository
                        .findByVehicleListingOrderByDisplayOrderAsc(
                                listing
                        );

        // Validate that the request array matches the listing's exact image IDs without duplicates
        validateReorderRequest(
                images,
                request.getImageIds()
        );

        // Map images by ID for fast O(1) lookup during reordering
        Map<Long, VehicleImage> imagesById =
                images.stream()
                        .collect(
                                Collectors.toMap(
                                        VehicleImage::getId,
                                        Function.identity()
                                )
                        );

        // Apply new indices based on position in request array
        for (int i = 0;
             i < request.getImageIds().size();
             i++) {

            Long imageId =
                    request.getImageIds().get(i);

            VehicleImage image =
                    imagesById.get(imageId);

            image.setDisplayOrder(i);
            image.setPrimaryImage(i == 0); // First item in array becomes primary
        }

        List<VehicleImage> savedImages =
                vehicleImageRepository.saveAll(images);

        // Sort saved entities by display order and map to response DTOs
        return savedImages.stream()
                .sorted(
                        (first, second) ->
                                Integer.compare(
                                        first.getDisplayOrder(),
                                        second.getDisplayOrder()
                                )
                )
                .map(imageMapper::toResponse)
                .toList();
    }

    // ==========================================
    // SHARED HELPERS
    // ==========================================

    private VehicleListing getOwnedListingWithImages(
            Long listingId
    ) {
        VehicleListing listing =
                vehicleListingService
                        .getActiveListingWithImages(listingId);

        User authenticatedUser =
                currentUserService.getAuthenticatedUser();

        vehicleListingService.verifyOwnership(
                listing,
                authenticatedUser
        );

        return listing;
    }

    private VehicleImage findImageInListing(
            VehicleListing listing,
            Long imageId
    ) {
        return listing.getImages()
                .stream()
                .filter(image ->
                        image.getId().equals(imageId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ImageNotFoundException(
                                "Image not found"
                        )
                );
    }

    private void validateImageFile(
            MultipartFile file
    ) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Image file must not be empty"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(
                contentType
        )) {

            throw new IllegalArgumentException(
                    "Only JPEG, PNG and WEBP images are supported"
            );
        }
    }

    private void validateReorderRequest(
            List<VehicleImage> images,
            List<Long> requestedImageIds
    ) {
        if (images.size() != requestedImageIds.size()) {
            throw new IllegalArgumentException(
                    "The reorder request must contain all listing images"
            );
        }

        Set<Long> requestedIds =
                new HashSet<>(requestedImageIds);

        if (requestedIds.size()
                != requestedImageIds.size()) {

            throw new IllegalArgumentException(
                    "Image IDs must not contain duplicates"
            );
        }

        Set<Long> actualIds =
                images.stream()
                        .map(VehicleImage::getId)
                        .collect(Collectors.toSet());

        if (!actualIds.equals(requestedIds)) {
            throw new IllegalArgumentException(
                    "All image IDs must belong to this listing"
            );
        }
    }

    /**
     * Resets sequence gaps in image positions following image deletion (0, 1, 2...).
     */
    private void normalizeImages(
            VehicleListing listing
    ) {
        List<VehicleImage> remainingImages =
                vehicleImageRepository
                        .findByVehicleListingOrderByDisplayOrderAsc(
                                listing
                        );

        for (int i = 0;
             i < remainingImages.size();
             i++) {

            VehicleImage image =
                    remainingImages.get(i);

            image.setDisplayOrder(i);
            image.setPrimaryImage(i == 0);
        }

        vehicleImageRepository.saveAll(
                remainingImages
        );
    }

    // Helper method to generate a unique, sanitized filename for disk/S3 storage
    private String generateStorageFilename(
            MultipartFile file
    ) {
        // Extract the original filename provided by the client (e.g., "my_car.png")
        String originalFilename =
                file.getOriginalFilename();

        // Validate that the filename exists and contains an extension dot
        if (originalFilename == null
                || !originalFilename.contains(".")) {

            throw new IllegalArgumentException(
                    "Uploaded file must have a valid filename"
            );
        }

        // Extract the file extension by cutting the string from the last dot to the end (e.g., ".png")
        String extension =
                originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );

        // Concatenate a random 128-bit UUID with the file extension to ensure a unique storage name
        // (e.g., "c9bf9e57-1685-4c89-bafb-ff5af830be8a.png")
        return UUID.randomUUID() + extension;
    }

    private VehicleImage createVehicleImage(
            VehicleListing listing,
            MultipartFile file,
            String storageFilename,
            boolean primaryImage,
            int displayOrder
    ) {
        // Create new VehicleImage entity and populate metadata fields
        VehicleImage vehicleImage =
                new VehicleImage();

        vehicleImage.setVehicleListing(listing);
        vehicleImage.setOriginalFilename(
                file.getOriginalFilename()
        );
        vehicleImage.setStorageFilename(
                storageFilename
        );
        vehicleImage.setContentType(
                file.getContentType()
        );
        vehicleImage.setFileSize(file.getSize());
        vehicleImage.setPrimaryImage(primaryImage);
        vehicleImage.setDisplayOrder(displayOrder);

        return vehicleImage;
    }
}