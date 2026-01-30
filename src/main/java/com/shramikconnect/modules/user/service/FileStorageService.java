package com.shramikconnect.modules.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption; // ✅ Standard Java NIO import
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    // Files will be stored in an 'uploads/kyc' folder in your project directory
    private final String uploadDir = "uploads/kyc/";

    public String saveFile(MultipartFile file, Long userId) throws IOException {
        Path path = Paths.get(uploadDir);
        
        // Create directory if it doesn't exist
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Generate a unique filename: e.g., 1_aadhaar.jpg
        String fileName = userId + "_" + file.getOriginalFilename();
        Path filePath = path.resolve(fileName);
        
        // Use the standard Java copy method
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
}