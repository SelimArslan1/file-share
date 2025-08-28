package com.file_share.service;

import com.file_share.dto.FileUploadResponse;
import com.file_share.entity.FileEntity;
import com.file_share.entity.User;
import com.file_share.repository.FileEntityRepository;
import com.file_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public FileService(UserRepository userRepository, FileEntityRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
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

}
