package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateRatingRequest;
import com.recipesharing.dto.request.UpdateRatingRequest;
import com.recipesharing.dto.response.RatingResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ratings")
@Tag(name = "Ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // ADD
    @Operation(summary = "Add rating to recipe", description = "Adds a new rating/review to a recipe by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rating created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Recipe not found"),
            @ApiResponse(responseCode = "409", description = "User already rated this recipe")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingResponse> add(
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateRatingRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(201)
                .body(ratingService.addRating(recipeId, request, user));
    }

    // UPDATE
    @Operation(summary = "Update recipe rating", description = "Updates the authenticated user's existing rating/review for a recipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the rating owner"),
            @ApiResponse(responseCode = "404", description = "Recipe or rating not found")
    })
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingResponse> update(
            @PathVariable Long recipeId,
            @Valid @RequestBody UpdateRatingRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ratingService.updateRating(recipeId, request, user));
    }

    // DELETE
    @Operation(summary = "Delete recipe rating", description = "Deletes the authenticated user's rating/review for a recipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the rating owner"),
            @ApiResponse(responseCode = "404", description = "Recipe or rating not found")
    })
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(
            @PathVariable Long recipeId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user
    ) {
        ratingService.deleteRating(recipeId, user);
        return ResponseEntity.noContent().build();
    }

    // PUBLIC
    @Operation(summary = "Get recipe ratings", description = "Retrieves paginated list of ratings/reviews for a specific recipe. Public endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<Page<RatingResponse>> getRatings(
            @PathVariable Long recipeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ratingService.getRatingsByRecipe(recipeId, pageable));
    }
}
