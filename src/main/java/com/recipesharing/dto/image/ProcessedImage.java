package com.recipesharing.dto.image;

/**
 * @param format "jpg", "png", etc.
 */
public record ProcessedImage(byte[] mainImage, byte[] thumbnailImage, String format) {
}