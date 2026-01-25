package com.recipesharing.controller;

import com.recipesharing.dto.request.LabelsListRequest;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.User;
import com.recipesharing.service.LabelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes/{recipeId}/labels")
@Tag(name = "Recipe Labels")
public class RecipeLabelController {

    private final LabelService labelService;

    public RecipeLabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PreAuthorize("hasRole('CHEF')")
    @PostMapping
    public ResponseEntity<RecipeResponse> addLabels(
            @PathVariable Long recipeId,
            @RequestBody LabelsListRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(labelService.addLabelsToRecipe(recipeId, request, currentUser));
    }

    @PreAuthorize("hasRole('CHEF')")
    @DeleteMapping("/{labelId}")
    public ResponseEntity<Void> removeLabel(
            @PathVariable Long recipeId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal User currentUser
    ) {
        labelService.removeLabelFromRecipe(recipeId, labelId, currentUser);
        return ResponseEntity.noContent().build();
    }
}