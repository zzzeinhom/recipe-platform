package com.recipesharing.controller;

import com.recipesharing.dto.request.LabelsListRequest;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/labels")
@Tag(name = "Recipe Labels", description = "API endpoints for managing recipe labels")
public class RecipeLabelController {

    private final LabelService labelService;

    public RecipeLabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    // ADD labels to recipe (owner only)
    /**
     * Add labels to a recipe. Owner (chef) only.
     * @param recipeId recipe id
     * @param request list of labels to add
     * @param currentUser authenticated principal
     * @return updated RecipeResponse
     */
    @Operation(summary = "Add labels to recipe", description = "Adds labels to a recipe. Only the recipe owner (chef) can add labels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Labels added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<RecipeResponse> addLabels(
            @PathVariable Long recipeId,
            @RequestBody LabelsListRequest request,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(labelService.addLabelsToRecipe(recipeId, request, currentUser));
    }

    // REMOVE label from recipe (owner only)
    /**
     * Remove a label from a recipe. Owner (chef) only.
     * @param recipeId recipe id
     * @param labelId label id to remove
     * @param currentUser authenticated principal
     * @return HTTP 204 No Content
     */
    @Operation(summary = "Remove label from recipe", description = "Removes a label from a recipe. Only the recipe owner (chef) can remove labels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Label removed successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User is not the recipe owner"),
            @ApiResponse(responseCode = "404", description = "Recipe or label not found")
    })
    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> removeLabel(
            @PathVariable Long recipeId,
            @PathVariable Long labelId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal User currentUser
    ) {
        labelService.removeLabelFromRecipe(recipeId, labelId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
