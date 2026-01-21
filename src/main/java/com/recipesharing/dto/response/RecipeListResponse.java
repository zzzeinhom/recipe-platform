package com.recipesharing.dto.response;

import com.recipesharing.entity.RecipeDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class RecipeListResponse {
    private Long id;
    private String title;
    private String description;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private RecipeDifficulty difficulty;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private String chefUsername;
}

