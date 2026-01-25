package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateRatingRequest;
import com.recipesharing.dto.request.UpdateRatingRequest;
import com.recipesharing.dto.response.RatingResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.RatingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingResponse> add(
            @PathVariable Long recipeId,
            @Valid @RequestBody CreateRatingRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(201)
                .body(ratingService.addRating(recipeId, request, user));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RatingResponse> update(
            @PathVariable Long recipeId,
            @Valid @RequestBody UpdateRatingRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ratingService.updateRating(recipeId, request, user));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(
            @PathVariable Long recipeId,
            @AuthenticationPrincipal User user
    ) {
        ratingService.deleteRating(recipeId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<RatingResponse>> getRatings(
            @PathVariable Long recipeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ratingService.getRatingsByRecipe(recipeId, pageable));
    }
}