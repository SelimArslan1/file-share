package com.file_share.controller;


import com.file_share.dto.FileUploadResponse;
import com.file_share.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public FileUploadResponse upload(@RequestParam("file") MultipartFile file) {

    }
    @GetMapping("download/{downloadToken}")
    public void download(@PathVariable String downloadToken) {

    }
}
