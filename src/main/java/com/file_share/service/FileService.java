package com.file_share.service;

import com.file_share.entity.FileEntity;
import com.file_share.repository.FileEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private FileEntityRepository fileRepository;

    public FileEntity saveFile(MultipartFile file) {
        try {
            FileEntity fileEntity = new FileEntity();

            fileEntity.setOriginalFileName();

            return fileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
