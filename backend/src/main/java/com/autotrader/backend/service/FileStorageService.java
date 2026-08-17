package com.autotrader.backend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// Marks this class as a Spring Service bean so it can be injected across the application
@Service
public class FileStorageService {

    // Injects the directory path configured in application.properties (e.g., "uploads")
    @Value("${app.upload.directory}")
    private String uploadDirectory;

    // Stores the absolute system Path representation of the target upload directory
    private Path uploadPath;

    // Runs automatically after constructor execution and field dependency injection
    @PostConstruct
    public void initialize() {
        try {
            // Converts the string path into an absolute, normalized system path
            // (e.g., converts "uploads" to "/home/user/autotrader/uploads")
            uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();

            // Creates the directory and missing parent folders on disk if they don't already exist
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            // Fails application startup quickly if local storage cannot be initialized
            throw new RuntimeException("Failed to initialize upload directory.", e);
        }
    }

    // Returns the absolute upload directory path to other components if requested
    public Path getUploadPath() {
        return uploadPath;
    }

    // Accepts a binary input stream and saves it to disk under storageFilename
    public Path saveFile(InputStream inputStream, String storageFilename) {
        try {
            // Combines the base upload path with the generated filename
            Path destination = uploadPath.resolve(storageFilename);

            // Copies raw bytes from stream to destination, overwriting if a file with the same name exists
            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Returns the complete file path destination on disk
            return destination;

        } catch (IOException e) {
            // Wraps IO errors during stream copy with contextual error message
            throw new RuntimeException("Failed to store file: " + storageFilename, e);
        }
    }

    // Deletes a specific file from disk using its storage filename
    public void deleteFile(String storageFilename) {
        try {
            // Resolves full target path for the requested file
            Path filePath = uploadPath.resolve(storageFilename);
            // Safely deletes the file if present without throwing an error if missing
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Throws runtime exception if deletion fails due to permission or lock issues
            throw new RuntimeException("Failed to delete file: " + storageFilename, e);
        }
    }
}