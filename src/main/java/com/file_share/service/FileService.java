package com.file_share.service;

import com.file_share.dto.FileUploadResponse;
import com.file_share.entity.Download;
import com.file_share.entity.FileEntity;
import com.file_share.entity.User;
import com.file_share.repository.DownloadRepository;
import com.file_share.repository.FileEntityRepository;
import com.file_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileService {

    private final FileEntityRepository fileRepository;
    private final UserRepository userRepository;
    private final DownloadRepository downloadRepository;

    public FileService(UserRepository userRepository, FileEntityRepository fileRepository, DownloadRepository downloadRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.downloadRepository = downloadRepository;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileUploadResponse saveFile(MultipartFile file, UUID uploaderId) {
        try {
            User uploader = userRepository.findById(uploaderId)
                    .orElseThrow(() -> new RuntimeException("Uploader not found"));


            // Saving the file to storage
            String storedFileName = UUID.randomUUID().toString();
            Path path = Paths.get(uploadDir, storedFileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


            // Saving metadata to the database
            FileEntity entity = new FileEntity();
            entity.setOriginalFileName(file.getOriginalFilename());
            entity.setStoredFileName(storedFileName);
            entity.setSize(file.getSize());
            entity.setUploader(uploader);
            entity.setExpirationTime(LocalDateTime.now().plusDays(1));
            entity.setDownloadToken(UUID.randomUUID().toString());

            fileRepository.save(entity);

            return new FileUploadResponse(entity.getDownloadToken());

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public ResponseEntity<Resource> download(String downloadToken, UUID downloaderId) {
        // Find file entity by downloadToken
        FileEntity fileEntity = fileRepository.findByDownloadToken(downloadToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        // Check expiration
        if (fileEntity.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Download link expired");
        }

        // Build file path
        Path path = Paths.get(uploadDir, fileEntity.getStoredFileName());

        try {
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

            User user = userRepository.findById(downloaderId).orElseThrow();

            // Log downloads
            Download download = new Download(fileEntity, user);
            downloadRepository.save(download);


            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileEntity.getOriginalFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    public void deleteFile(String downloadToken) {

        FileEntity fileToDelete = fileRepository.findByDownloadToken(downloadToken)
                .orElseThrow(() -> new RuntimeException("File not found with token: " + downloadToken));

        Path path = Paths.get(uploadDir, fileToDelete.getStoredFileName());

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete the file.");
        }

        // Set as deleted
        fileToDelete.setDeleted(true);
        fileRepository.save(fileToDelete);
    }

}
