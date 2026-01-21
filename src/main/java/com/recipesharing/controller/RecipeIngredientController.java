package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateIngredientRequest;
import com.recipesharing.dto.request.UpdateIngredientRequest;
import com.recipesharing.dto.response.IngredientResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
@Tag(name = "Recipe Ingredients", description = "API endpoints for managing recipe ingredients")
public class RecipeIngredientController {

    private final IngredientService ingredientService;

    public RecipeIngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    // GET ingredients (public)
    /**
     * Get ingredients for a recipe.
     * @param recipeId recipe id
     * @return list of IngredientResponse
     */
    @Operation(summary = "Get recipe ingredients", description = "Retrieves all ingredients for a specific recipe. Public endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getIngredients(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ingredientService.getIngredientsByRecipeId(recipeId));
    }

    // ADD ingredient (owner only)
    /**
     * Add ingredient to a recipe (owner only).
     * @param recipeId recipe id
     * @param request create ingredient payload
     * @param currentUser authenticated principal
     * @return created IngredientResponse with HTTP 201
     */
    @Operation(summary = "Add ingredient to recipe", description = "Adds a new ingredient to a recipe. Only the recipe owner (chef) can add ingredients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingredient added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<IngredientResponse> addIngredient(
            @PathVariable Long recipeId,
            @RequestBody CreateIngredientRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        IngredientResponse created = ingredientService.addIngredientToRecipe(recipeId, request, currentUser);
        return ResponseEntity.status(201).body(created);
    }

    // UPDATE ingredient (owner only) - path contains ingredient id for clarity
    /**
     * Update an ingredient in a recipe (owner only).
     * @param recipeId recipe id
     * @param ingredientId ingredient id
     * @param request update payload
     * @param currentUser authenticated principal
     * @return updated IngredientResponse
     */
    @Operation(summary = "Update recipe ingredient", description = "Updates an existing ingredient in a recipe. Only the recipe owner (chef) can update ingredients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe or ingredient not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponse> updateIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @RequestBody UpdateIngredientRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        // ensure request id matches path (optional guard)
        request.setId(ingredientId);
        return ResponseEntity.ok(ingredientService.updateIngredientInRecipe(recipeId, request, currentUser));
    }

    // DELETE ingredient (owner only)
    /**
     * Delete an ingredient from a recipe (owner only).
     * @param recipeId recipe id
     * @param ingredientId ingredient id
     * @param currentUser authenticated principal
     * @return HTTP 204 No Content
     */
    @Operation(summary = "Delete recipe ingredient", description = "Removes an ingredient from a recipe. Only the recipe owner (chef) can delete ingredients.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ingredient deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe or ingredient not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        ingredientService.deleteIngredientFromRecipe(recipeId, ingredientId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
