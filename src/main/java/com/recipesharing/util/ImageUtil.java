package com.recipesharing.util;

import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.service.LocalFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImageUtil {
    private final LocalFileStorageService fileStorageService;
    private final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public ImageUtil(LocalFileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void cleanupRecipeImageFiles(Recipe recipe) {
        String imageUrl = recipe.getImageUrl();
        String thumbnailUrl = recipe.getThumbnailUrl();

        try {
            if (imageUrl != null && !imageUrl.isBlank()) {
                fileStorageService.delete(imageUrl);
            }
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                fileStorageService.delete(thumbnailUrl);
            }
        } catch (Exception e) {
            logger.warn("Failed to cleanup recipe image files for recipe id {}: {}", recipe.getId(), e.getMessage());
        }
    }

    public void cleanupUserImageFiles(User user) {
        String imageUrl = user.getProfileImage();

        try {
            if (imageUrl != null && !imageUrl.isBlank()) {
                fileStorageService.delete(imageUrl);
            }
        } catch (Exception e) {
            logger.warn("Failed to cleanup recipe image files for recipe id {}: {}", user.getId(), e.getMessage());
        }
    }
}

