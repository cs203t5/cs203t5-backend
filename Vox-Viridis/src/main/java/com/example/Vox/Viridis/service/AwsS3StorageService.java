package com.example.Vox.Viridis.service;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.Vox.Viridis.config.AWSConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AwsS3StorageService implements StorageService {
    private final String baseUrl;
    private final String bucketName;
    private final AmazonS3 client;

    public AwsS3StorageService(AWSConfig config, 
            @Value("${aws.s3-base-url}") String baseUrl,
            @Value("${aws.s3-bucket-name}") String bucketName) {
        client = config.amazonS3();
        this.baseUrl = baseUrl;
        this.bucketName = bucketName;
    }

    public String getUrl(String filename) {
        return baseUrl + filename;
    }

    @Override
    public void putObject(String filename, MultipartFile file) {
        ObjectMetadata md = new ObjectMetadata();
        md.setContentLength(file.getSize());
        md.setContentType(file.getContentType());
        
        try {
            var putObjectRequest = new PutObjectRequest(bucketName, filename, file.getInputStream(), md).withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(putObjectRequest);
            log.info("upload file into AWS S3: " + filename);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public void deleteObject(String filename) {
        client.deleteObject(bucketName, filename);
        log.info("delete file in AWS S3: " + filename);
    }
}
