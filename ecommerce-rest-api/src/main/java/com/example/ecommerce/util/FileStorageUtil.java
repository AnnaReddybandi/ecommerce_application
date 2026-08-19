package com.example.ecommerce.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * FileStorageUtil
 * Utility component for handling product image uploads and deletions
 */
@Component
public class FileStorageUtil {

    private static final Logger log = LoggerFactory.getLogger(FileStorageUtil.class);

    @Value("${file.upload.dir:uploads/products/}")
    private String uploadDir;

    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Upload directory created: {}", uploadDir);
            }
        } catch (IOException e) {
            log.error("Failed to create upload directory", e);
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!validateFile(file)) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, GIF, WebP) are allowed");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File stored successfully: {}", fileName);
        return uploadDir + fileName;
    }

    public void deleteFile(String filePathStr) {
        if (filePathStr == null || filePathStr.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(filePathStr);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePathStr);
        } catch (IOException e) {
            log.warn("Could not delete file: {}", filePathStr, e);
        }
    }

    public boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
