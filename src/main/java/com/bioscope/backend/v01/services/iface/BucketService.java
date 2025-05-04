package com.bioscope.backend.v01.services.iface;

import org.springframework.web.multipart.MultipartFile;

public interface BucketService {

    String uploadFile(MultipartFile file);
    String preSignedUrl(String fileName);
    void deleteFile(String fileName);
}
