package org.example.revplaycatalogservice.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "uploads/";

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            Files.copy(file.getInputStream(), filePath);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file. Please try again!", e);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIR + fileName));
            } catch (IOException e) {
                System.err.println("Failed to delete physical file: " + fileName);
            }
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }
}