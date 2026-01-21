package com.recipesharing.controller;

import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorites")
@RestController
@RequestMapping("/api")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // ============================
    // ADD FAVORITE
    // ============================
    @Operation(summary = "Add recipe to favorites", description = "Adds a recipe to the authenticated user's favorites list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe added to favorites successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Recipe not found"),
            @ApiResponse(responseCode = "409", description = "Recipe already in favorites")
    })
    @PostMapping("/recipes/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        favoriteService.addFavorite(id, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ============================
    // REMOVE FAVORITE
    // ============================
    @Operation(summary = "Remove recipe from favorites", description = "Removes a recipe from the authenticated user's favorites list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe removed from favorites successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Recipe not found or not in favorites")
    })
    @DeleteMapping("/recipes/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        favoriteService.removeFavorite(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}