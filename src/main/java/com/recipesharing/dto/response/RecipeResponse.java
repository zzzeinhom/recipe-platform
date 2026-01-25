package com.recipesharing.dto.response;

import com.recipesharing.entity.RecipeDifficulty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class RecipeResponse {
    private Long id;
    private String title;
    private String description;
    private List<String> labels;
    private List<IngredientResponse> ingredients;
    private String instructions;
    private Integer prepTime;
    private Integer cookTime;
    private Integer servings;
    private RecipeDifficulty difficulty;
    private LocalDateTime createdAt;
    private Integer viewCount;
    private Long favoritesCount;
    private Long ratingCount;
    private BigDecimal averageRating;
    private String imageUrl;
    private String thumbnailUrl;
    private String chefUsername;
    private Boolean isFavorite;

    public RecipeResponse() {

    }
}

