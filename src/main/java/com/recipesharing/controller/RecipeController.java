package com.recipesharing.controller;

import com.recipesharing.dto.request.CreateRecipeRequest;
import com.recipesharing.dto.request.UpdateRecipeRequest;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.RecipeDifficulty;
import com.recipesharing.entity.User;
import com.recipesharing.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // CREATE (chef only)
    /**
     * Create a new recipe. Restricted to users with CHEF role.
     * @param request create recipe payload
     * @param currentUser authenticated user principal
     * @return created RecipeResponse with HTTP 201
     */
    @Operation(summary = "Create a new recipe", description = "Creates a new recipe. Only users with CHEF role can create recipes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User does not have CHEF role")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<RecipeResponse> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        RecipeResponse created = recipeService.createRecipe(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET (public) by id
    /**
     * Get recipe details by id. Public endpoint.
     * @param id recipe id
     * @param currentUser optional authenticated user
     * @return RecipeResponse with HTTP 200
     */
    @Operation(summary = "Get recipe by id", description = "Retrieves detailed information about a specific recipe. Public endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id,@Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(recipeService.getRecipeById(id, currentUser));
    }

    // GET (public) list - lightweight, pageable
    /**
     * Get paginated list of recipes (lightweight listing).
     * @param pageable paging and sorting information
     * @return page of RecipeListResponse with HTTP 200
     */
    @Operation(summary = "Get all recipes paginated", description = "Retrieves a paginated list of all recipes (lightweight listing with basic info only).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    @GetMapping
    public ResponseEntity<Page<RecipeListResponse>> getAllRecipes(
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.getAllRecipes(pageable));
    }

    // SEARCH (public) with filters - lightweight, pageable
    /**
     * Search recipes with optional filters. Returns lightweight listing.
     * @param keyword text search
     * @param labels list of label names
     * @param chefId optional chef id filter
     * @param difficulty optional difficulty filter
     * @param pageable paging information
     * @return page of RecipeListResponse with HTTP 200
     */
    @Operation(summary = "Search recipes with filters", description = "Searches recipes with optional keyword, labels, chef, and difficulty filters. Returns paginated lightweight results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<RecipeListResponse>> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> labels,
            @RequestParam(required = false) Long chefId,
            @RequestParam(required = false) RecipeDifficulty difficulty,
            @PageableDefault(
                    size = 15,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.searchRecipes(
                keyword,
                labels,
                chefId,
                difficulty,
                pageable
        ));
    }


    // UPDATE (owner only)
    /**
     * Update a recipe. Only the owner (chef) may update.
     * @param request update payload
     * @param currentUser authenticated user
     * @return updated RecipeResponse with HTTP 200
     */
    @Operation(summary = "Update a recipe", description = "Updates an existing recipe. Only the recipe owner (chef) can update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PutMapping
    public ResponseEntity<RecipeResponse> updateRecipe(
            @Valid @RequestBody UpdateRecipeRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        return ResponseEntity.ok(recipeService.updateRecipe(request, currentUser));
    }

    // DELETE (owner only)
    /**
     * Delete a recipe. Only the owner (chef) may delete.
     * @param id recipe id
     * @param currentUser authenticated user
     * @return HTTP 204 No Content on success
     */
    @Operation(summary = "Delete a recipe", description = "Deletes a recipe. Only the recipe owner (chef) can delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser    ) {
        recipeService.deleteRecipe(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
