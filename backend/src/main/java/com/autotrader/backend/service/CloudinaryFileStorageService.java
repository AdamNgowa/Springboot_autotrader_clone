
package com.autotrader.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
@Profile("prod")
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    public CloudinaryFileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

     @Override
    public void saveFile(InputStream inputStream, String storageFilename) {
        String publicId = toPublicId(storageFilename);

        try {
            byte[] fileBytes = inputStream.readAllBytes();

            Map<?, ?> result = cloudinary.uploader().upload(
                    fileBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", false,
                            "unique_filename", false
                    )
            );

            Object returnedPublicId = result.get("public_id");

            if (!publicId.equals(returnedPublicId)) {
                throw new IllegalStateException(
                        "Cloudinary returned an unexpected public ID."
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to upload image to Cloudinary: " + storageFilename,
                    e
            );
        }
    }

    @Override
    public void deleteFile(String storageFilename) {
        String publicId = toPublicId(storageFilename);

        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );

            Object status = result.get("result");

            if (!"ok".equals(status) && !"not found".equals(status)) {
                throw new IllegalStateException(
                        "Cloudinary did not confirm image deletion."
                );
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to delete image from Cloudinary: " + storageFilename,
                    e
            );
        }
    }

    @Override
    public String getFileUrl(String storageFilename) {
        String publicId = toPublicId(storageFilename);
        String extension = getExtension(storageFilename);

        return cloudinary.url()
                .secure(true)
                .generate(publicId + "." + extension);
    }

    private String toPublicId(String storageFilename) {
        int extensionIndex = storageFilename.lastIndexOf('.');

        if (extensionIndex <= 0 || extensionIndex == storageFilename.length() - 1) {
            throw new IllegalArgumentException(
                    "Storage filename must include a file extension."
            );
        }

        return storageFilename.substring(0, extensionIndex);
    }

    private String getExtension(String storageFilename) {
        int extensionIndex = storageFilename.lastIndexOf('.');

        if (extensionIndex <= 0 || extensionIndex == storageFilename.length() - 1) {
            throw new IllegalArgumentException(
                    "Storage filename must include a file extension."
            );
        }

        return storageFilename.substring(extensionIndex + 1).toLowerCase();
    }
}