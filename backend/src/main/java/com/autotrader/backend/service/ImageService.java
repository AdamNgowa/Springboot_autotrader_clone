package com.autotrader.backend.service;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.repository.VehicleImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


@Service

public class ImageService {
    private final CurrentUserService currentUserService;
    private final VehicleListingService vehicleListingService;
    private final VehicleImageRepository vehicleImageRepository;
    private final FileStorageService fileStorageService;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );


    public ImageService(CurrentUserService currentUserService, VehicleListingService vehicleListingService, VehicleImageRepository vehicleImageRepository, FileStorageService fileStorageService) {
        this.currentUserService = currentUserService;
        this.vehicleListingService = vehicleListingService;
        this.vehicleImageRepository = vehicleImageRepository;
        this.fileStorageService = fileStorageService;
    }
    /*
        CORE BUSINESS OPERATIONS
     */

    public void uploadImage(Long listingId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(" Image file must not be empty");
        }

        String contentType = file.getContentType();
        if(contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG,PNG and WEBP images are supported");
        }

        //Retrieve the listing and make sure it exists and has not been deleted
        VehicleListing listing = vehicleListingService.getActiveListing(listingId);

        //Retrieve the authenticated user
        User authenticatedUser = currentUserService.getAuthenticatedUser();

        //Ensure the authenticated user owns the listing
        vehicleListingService.verifyOwnership(listing,authenticatedUser);

        String storageFilename = generateStorageFilename(file);

        try {
                fileStorageService.saveFile(
                        file.getInputStream(),
                        storageFilename
                );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read upload file",e);
        }
    }

    private String generateStorageFilename(MultipartFile file) {
        //Get original file name supplied by the client
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Uploaded file must have a valid filename");
        }

        String extension =
                originalFilename.substring(
                        originalFilename.lastIndexOf(".")
                );

        return UUID.randomUUID() + extension;


    }
}
