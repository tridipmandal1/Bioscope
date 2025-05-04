package com.bioscope.backend.v01.services.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.bioscope.backend.v01.services.iface.BucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;

@Service
@Slf4j
public class BucketServiceImpl implements BucketService {

    private final AmazonS3 client;

    public BucketServiceImpl(AmazonS3 client) {
        this.client = client;
    }

    @Value("${app.s3.bucket}")
    private String BUCKET_NAME;
    @Value("${file.max-size}")
    private long MAX;

    private final long MAX_SIZE = MAX * 1024 * 1024;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is null or empty");
        }
//        if (file.getSize() > MAX_SIZE) {
//            throw new RuntimeException("File size exceeds the limit");
//        }
        String filename = file.getOriginalFilename()+ ".png";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        try {
            PutObjectRequest request = new PutObjectRequest(
                    BUCKET_NAME,
                    filename,
                    file.getInputStream(),
                    metadata
            );
            PutObjectResult result =
                    client.putObject(request);
            log.info("QR uploaded to S3: {}", result.getETag());
            return filename;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String preSignedUrl(String fileName) {

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        final int duration = 1; //hours
        expTimeMillis += duration * 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest request =
               new  GeneratePresignedUrlRequest(
                       BUCKET_NAME,
                       fileName,
                       HttpMethod.GET)
                       .withExpiration(expiration);

        URL url = client.generatePresignedUrl(request);


        return url.toString();
    }

    @Override
    public void deleteFile(String fileName) {
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET_NAME, fileName);
        client.deleteObject(request);
    }
}
