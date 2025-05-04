package com.bioscope.backend.v01.services.impl;

import com.bioscope.backend.v01.services.iface.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeServiceImpl implements QRCodeService {


    @Override
    public MultipartFile generateQRCode(String data, String identifier) throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        bufferedImage.createGraphics().fillRect(0, 0, 200, 200);
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setColor(Color.BLACK);

        for (int x = 0; x < 200; x++) {

            for (int y = 0; y < 200; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", outputStream);
        byte [] byteImage = outputStream.toByteArray();
        return new MockMultipartFile(identifier, identifier, "image/png", byteImage);
    }

    @Override
    public String readQRCode(MultipartFile file) throws IOException {
        ByteArrayInputStream inputByte = new ByteArrayInputStream(file.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputByte);

        BufferedImageLuminanceSource source =
                new BufferedImageLuminanceSource(bufferedImage);

        HybridBinarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap bitmap = new BinaryBitmap(binarizer);
        MultiFormatReader reader = new MultiFormatReader();
        try {
            return reader.decode(bitmap).getText();
        } catch (Exception e) {
            throw new IOException("Error reading QR code ");
        }
    }
}
