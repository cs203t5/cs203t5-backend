package com.example.Vox.Viridis.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void putObject(String filename, MultipartFile file);
}
