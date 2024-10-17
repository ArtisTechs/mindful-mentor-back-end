package com.example.mindful_mentor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String uploadDir = "uploads/"; // Define your upload directory

    // Method to store file
    public String storeFile(MultipartFile file) {
        try {
            // Create unique file name
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);

            // Ensure the upload directory exists
            Files.createDirectories(path.getParent());

            // Store the file on the system
            Files.write(path, file.getBytes());

            // Return the file path or URL
            return path.toString(); // This returns the file path, but can be adjusted for URLs
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
