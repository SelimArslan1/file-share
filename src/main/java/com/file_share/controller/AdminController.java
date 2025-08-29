package com.file_share.controller;


import com.file_share.config.JwtUtil;
import com.file_share.repository.UserRepository;
import com.file_share.service.FileService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AdminController(FileService fileService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.fileService = fileService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @DeleteMapping("/delete/{downloadToken}")
    public ResponseEntity<String> deleteFile(@PathVariable String downloadToken,
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

            fileService.deleteFile(downloadToken);
            return ResponseEntity.ok("File deleted successfully.");

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
