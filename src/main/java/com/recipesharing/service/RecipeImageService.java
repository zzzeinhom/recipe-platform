package com.recipesharing.service;

import com.recipesharing.dto.image.ProcessedImage;
import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.util.ImageUtil;
import com.recipesharing.util.ValidationUti;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class RecipeImageService {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final ImageProcessingService imageProcessingService;
    private final LocalFileStorageService fileStorageService;
    private final ImageUtil imageUtil;
    private final ValidationUti validationUti;
    public RecipeImageService(RecipeRepository recipeRepository, RecipeMapper recipeMapper, ImageProcessingService imageProcessingService, LocalFileStorageService fileStorageService, ImageUtil imageUtil, ValidationUti validationUti) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.imageProcessingService = imageProcessingService;
        this.fileStorageService = fileStorageService;
        this.imageUtil = imageUtil;
        this.validationUti = validationUti;
    }

    @Transactional
    public RecipeResponse uploadRecipeImage(Long recipeId, MultipartFile imageFile, User currentUser) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        validationUti.validateRecipeOwnership(recipe, currentUser);

        ProcessedImage processed = imageProcessingService.process(imageFile);

        String dir = "recipes/" + recipeId;
        String mainFilename = "image." + processed.format();
        String thumbFilename = "thumbnail." + processed.format();

        String mainUrl = fileStorageService.store(processed.mainImage(), dir, mainFilename);
        String thumbUrl = fileStorageService.store(processed.thumbnailImage(), dir, thumbFilename);

        imageUtil.cleanupRecipeImageFiles(recipe);

        recipe.setImageUrl(mainUrl);
        recipe.setThumbnailUrl(thumbUrl);
        Recipe saved = recipeRepository.save(recipe);

        return recipeMapper.toRecipeResponse(saved);
    }


    @Transactional
    public void deleteRecipeImage(Long recipeId, User currentUser) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        validationUti.validateRecipeOwnership(recipe, currentUser);

        recipe.setImageUrl(null);
        recipe.setThumbnailUrl(null);
        recipeRepository.save(recipe);

        imageUtil.cleanupRecipeImageFiles(recipe);
    }
}
