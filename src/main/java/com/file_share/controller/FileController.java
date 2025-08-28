package com.file_share.controller;


import com.file_share.config.JwtUtil;
import com.file_share.dto.FileUploadResponse;
import com.file_share.repository.UserRepository;
import com.file_share.service.FileService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("files")
public class FileController {

    private final FileService fileService;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public FileController(FileService fileService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String jwtHeader) {

        String token = jwtHeader.replace("Bearer ", "");

        try {

            String email = jwtUtil.extractEmail(token);

            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (userRepository.findByEmail(email).isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UUID uploaderId = userRepository.findByEmail(email).orElseThrow().getId();

            FileUploadResponse response = fileService.saveFile(file, uploaderId);
            return ResponseEntity.ok(response);

        } catch (JwtException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("download/{downloadToken}")
    public void download(@PathVariable String downloadToken) {

    }
}
