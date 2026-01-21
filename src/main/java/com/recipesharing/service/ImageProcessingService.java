package com.recipesharing.service;

import com.recipesharing.dto.image.ProcessedImage;
import com.recipesharing.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@Service
public class ImageProcessingService {

    @Value("${file.max-size-bytes}")
    private long maxSizeBytes;

    @Value("${image.max-width}")
    private int maxWidth;

    @Value("${image.thumbnail-width}")
    private int thumbnailWidth;

    @Value("${image.thumbnail-height}")
    private int thumbnailHeight;

    @Value("${image.allowed-extensions}")
    private List<String> allowedExtensions;

    public ProcessedImage process(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new FileStorageException("No file provided");
        }

        if (multipartFile.getSize() > maxSizeBytes) {
            throw new FileStorageException("File is too large (max " + maxSizeBytes + " bytes)");
        }

        String contentType = multipartFile.getContentType();
        if (contentType == null || allowedExtensions.stream().noneMatch(contentType::contains)) {
            throw new FileStorageException("Unsupported file type: " + contentType);
        }

        String format = detectFormatFromContentType(contentType);

        try (InputStream in = multipartFile.getInputStream();
             ByteArrayOutputStream mainOut = new ByteArrayOutputStream();
             ByteArrayOutputStream thumbOut = new ByteArrayOutputStream()) {

            BufferedImage original = ImageIO.read(in);
            if (original == null) {
                throw new FileStorageException("Uploaded file is not a valid image");
            }

            int originalWidth = original.getWidth();
            int actualMaxWidth = Math.min(maxWidth, Math.max(1, originalWidth));

            // Resize main image preserving aspect ratio (max width)
            Thumbnails.of(original)
                    .size(actualMaxWidth, Integer.MAX_VALUE)
                    .outputFormat(format)
                    .toOutputStream(mainOut);

            // Create thumbnail (center-crop style: use size and keep aspect by cropping)
            Thumbnails.of(original)
                    .size(thumbnailWidth, thumbnailHeight)
                    .crop(net.coobird.thumbnailator.geometry.Positions.CENTER)
                    .outputFormat(format)
                    .toOutputStream(thumbOut);

            return new ProcessedImage(mainOut.toByteArray(), thumbOut.toByteArray(), format);

        } catch (IOException e) {
            throw new FileStorageException("Failed to process image", e);
        }
    }

    private String detectFormatFromContentType(String contentType) {
        if (contentType.contains("jpeg") || contentType.contains("jpg")) return "jpeg";
        if (contentType.contains("png")) return "png";
        if (contentType.contains("webp")) return "webp";
        return "jpg";
    }
}