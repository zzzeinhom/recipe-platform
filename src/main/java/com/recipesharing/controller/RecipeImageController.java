package com.recipesharing.controller;

import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.RecipeImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recipes/{recipeId}/image")
@Tag(name = "Recipe Image Management")
public class RecipeImageController {

    private final RecipeImageService recipeImageService;

    public RecipeImageController(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    @PreAuthorize("hasRole('CHEF')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> uploadImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal User currentUser
    ) {
        RecipeResponse resp = recipeImageService.uploadRecipeImage(recipeId, image, currentUser);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal User currentUser
    ) {
        recipeImageService.deleteRecipeImage(recipeId, currentUser);
        return ResponseEntity.noContent().build();
    }
}