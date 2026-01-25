package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateRecipeRequest;
import com.recipesharing.dto.request.UpdateRecipeRequest;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.RecipeDifficulty;
import com.recipesharing.entity.User;
import com.recipesharing.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recipes", description = "Recipe management and browsing")
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        RecipeResponse created = recipeService.createRecipe(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(recipeService.getRecipeById(id, currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<RecipeListResponse>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.getAllRecipes(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RecipeListResponse>> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> labels,
            @RequestParam(required = false) Long chefId,
            @RequestParam(required = false) RecipeDifficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.searchRecipes(
                keyword,
                labels,
                chefId,
                difficulty,
                pageable
        ));
    }

    @PreAuthorize("hasRole('CHEF')")
    @PutMapping
    public ResponseEntity<RecipeResponse> updateRecipe(
            @Valid @RequestBody UpdateRecipeRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(recipeService.updateRecipe(request, currentUser));
    }

    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        recipeService.deleteRecipe(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}