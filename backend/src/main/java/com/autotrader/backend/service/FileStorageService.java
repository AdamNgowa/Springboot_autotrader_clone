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

@Service
public class FileStorageService {
    @Value("${app.upload.directory}")
    private String uploadDirectory;

    private Path uploadPath;

    @PostConstruct
    public void initialize() {
        try {
            /*
            .toAbsolutePath() takes whatever Paths.get() and converts it into an absolute vairable
            Suppose it finds 'uploads' in the target location it'll convert it into e.g '/home/user/autotrader/uploads'

            .normalize() removes any unnecessary path elements
             */
            uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            //If file already exists nothing bad happens and exception is thrown ,
            // If it doesn't exist however Java creates it
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize upload directory.", e);
        }
    }



    public Path getUploadPath() {
        return uploadPath;
    }

    public Path saveFile(InputStream inputStream, String storageFilename) {
        try {
            Path destination = uploadPath.resolve(storageFilename);

            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return destination;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + storageFilename, e);
        }
    }

    public void deleteFile(String storageFilename) {
        try {
            Path filePath = uploadPath.resolve(storageFilename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: "+storageFilename,e);
        }

    }

}