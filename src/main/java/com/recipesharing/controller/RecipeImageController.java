package com.recipesharing.controller;

import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.RecipeImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/recipes/{recipeId}/image")
@Tag(name = "Recipe Image Management", description = "API endpoints for managing recipe images")
public class RecipeImageController {

    private final RecipeImageService recipeImageService;

    public RecipeImageController(RecipeImageService recipeImageService) {
        this.recipeImageService = recipeImageService;
    }

    // UPLOAD or REPLACE image (owner only)
    /**
     * Upload or replace the image for a recipe. Owner (chef) only.
     * @param recipeId recipe id
     * @param image multipart image file
     * @param currentUser authenticated principal
     * @return updated RecipeResponse with HTTP 200
     */
    @Operation(summary = "Upload or replace recipe image", description = "Uploads or replaces the image for a recipe. Only the recipe owner (chef) can upload images.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> uploadImage(
            @PathVariable Long recipeId,
            @RequestParam("image") MultipartFile image,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        RecipeResponse resp = recipeImageService.uploadRecipeImage(recipeId, image, currentUser);
        return ResponseEntity.ok(resp);
    }

    // DELETE image (owner only)
    /**
     * Delete the recipe image. Owner (chef) only.
     * @param recipeId recipe id
     * @param currentUser authenticated principal
     * @return HTTP 204 No Content on success
     */
    @Operation(summary = "Delete recipe image", description = "Deletes the image for a recipe. Only the recipe owner (chef) can delete images.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long recipeId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        recipeImageService.deleteRecipeImage(recipeId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
