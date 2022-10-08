package com.example.Vox.Viridis.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String CAMPAIGNS_DIR = "campaigns/";
    
    String getUrl(String filename);
    void putObject(String filename, MultipartFile file);
    void deleteObject(String filename);
}
