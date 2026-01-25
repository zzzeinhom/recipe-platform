package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateIngredientRequest;
import com.recipesharing.dto.request.UpdateIngredientRequest;
import com.recipesharing.dto.response.IngredientResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.IngredientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
@Tag(name = "Recipe Ingredients")
public class RecipeIngredientController {

    private final IngredientService ingredientService;

    public RecipeIngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getIngredients(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ingredientService.getIngredientsByRecipeId(recipeId));
    }

    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<IngredientResponse> addIngredient(
            @PathVariable Long recipeId,
            @RequestBody CreateIngredientRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        IngredientResponse created = ingredientService.addIngredientToRecipe(recipeId, request, currentUser);
        return ResponseEntity.status(201).body(created);
    }

    @PreAuthorize("hasRole('CHEF')")
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @RequestBody UpdateIngredientRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        request.setId(ingredientId);
        return ResponseEntity.ok(ingredientService.updateIngredientInRecipe(recipeId, request, currentUser));
    }

    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @AuthenticationPrincipal User currentUser
    ) {
        ingredientService.deleteIngredientFromRecipe(recipeId, ingredientId, currentUser);
        return ResponseEntity.noContent().build();
    }
}