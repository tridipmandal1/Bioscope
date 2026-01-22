package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.services.iface.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2BucketService implements BucketService {

    @Value("${app.s3.bucket}")
    private String BUCKET_NAME;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;




    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is null or empty");
        }

        String original = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow( () -> new UnsupportedMediaTypeStatusException("File name missing"))
                .toLowerCase();

        String contentType =
                Optional.ofNullable(file.getContentType())
                        .orElseThrow(() -> new UnsupportedMediaTypeStatusException("File type missing"));

        String filename = UUID.randomUUID() + "-" + original;

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(filename)
                        .contentType(contentType)
                        .build();

        try {
            var resp =  s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return preSignedUrl(filename);
    }

    @Override
    public String preSignedUrl(String fileName) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fileName)
                        .build();
        PresignedGetObjectRequest objectRequest =
                s3Presigner.presignGetObject(
                        builder ->
                                builder.getObjectRequest(getObjectRequest)
                                        .signatureDuration(Duration.ofMinutes(15))
                                        .build()
                );

        return objectRequest.url().toString();
    }

    @Override
    public void deleteFile(String fileName) {

        DeleteObjectRequest deleteRequest =
                DeleteObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fileName)
                        .build();
        var resp = s3Client.deleteObject(deleteRequest);
    }
}
