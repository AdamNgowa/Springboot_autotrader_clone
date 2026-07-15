package com.autotrader.backend.service;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
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
        -----------------------------------
             CORE BUSINESS OPERATIONS
        -----------------------------------
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

        /*
        Here is where we actually save th image itself

        Try adding the file to the file system (Could be local .i.e on disk or remote like amazon S3)

        If saving file to file system fails throw an exception, here we throw an IOException
         */
        try {
                fileStorageService.saveFile(
                        file.getInputStream(),
                        storageFilename
                );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read upload file",e);
        }

        //vehicleImageRepository.existsByVehicleListing(listing) returns true or false
        boolean listingAlreadyHasImages =
                vehicleImageRepository.existsByVehicleListing(listing);
        //When the query returns false , the primary image should be toggled to true and vice versa
        boolean primaryImage = !listingAlreadyHasImages;

        /*
        Here we save the entity data. Not the actual image but the image's information.
        Things like original and storage file names,content type etc.

        Try saving the image metadata, if anything goes wrong return exception.
        Here we actually rethrow the exception so that we can have the cause of the issue

        We call deleteFile from file storage here as for the sake of double persistence
         */
        try {
            VehicleImage vehicleImage = createVehicleImage(listing,file,storageFilename,primaryImage);
            vehicleImageRepository.save(vehicleImage);

        } catch (Exception e) {
            fileStorageService.deleteFile(storageFilename);
            throw e;
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

    private VehicleImage createVehicleImage(
            VehicleListing listing,
            MultipartFile file,
            String storageFilename,
            boolean primaryImage
    ){
        //Create new vehicle image object
        VehicleImage vehicleImage = new VehicleImage();

        //Populate fields for the newly created object
        vehicleImage.setPrimaryImage(primaryImage);
        vehicleImage.setVehicleListing(listing);
        vehicleImage.setOriginalFilename(file.getOriginalFilename());
        vehicleImage.setStorageFilename(storageFilename);
        vehicleImage.setContentType(file.getContentType());
        vehicleImage.setFileSize(file.getSize());

        return vehicleImage;
    }
}
