package com.bioscope.backend.v01.services.iface;

import com.google.zxing.WriterException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface QRCodeService {

    MultipartFile generateQRCode(String data, String identifier) throws WriterException, IOException;
    String readQRCode(MultipartFile file) throws IOException;
}
