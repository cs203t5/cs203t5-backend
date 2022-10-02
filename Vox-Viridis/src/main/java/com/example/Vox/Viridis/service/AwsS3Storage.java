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
public class AwsS3Storage implements StorageService {
    private static final String BUCKET_NAME = "voxviridis";
    public static final String CAMPAIGNS_DIR = "campaigns/";
    @Value("${aws.s3-base-url}")
    private String BASE_URL;
    private final AmazonS3 client;

    public AwsS3Storage(AWSConfig config) {
        client = config.amazonS3();
    }

    /// @Return: URL to the file in AWS
    @Override
    public String putObject(String filename, MultipartFile file) {
        ObjectMetadata md = new ObjectMetadata();
        md.setContentLength(file.getSize());
        md.setContentType(file.getContentType());
        
        try {
            var putObjectRequest = new PutObjectRequest(BUCKET_NAME, filename, file.getInputStream(), md).withCannedAcl(CannedAccessControlList.PublicRead);
            client.putObject(putObjectRequest);
            log.info("upload file into AWS S3: " + filename);
            return BASE_URL + filename;
        } catch (IOException ioException) {
            log.error("Error uploading file '" + filename + "'", ioException);
            throw new RuntimeException(ioException);
        } catch (RuntimeException exception) {
            log.error("Error uploading file '" + filename + "'", exception);
            throw exception;
        }
    }
}
