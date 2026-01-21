package com.recipesharing.dto.mapper;

import com.recipesharing.dto.request.CreateIngredientRequest;
import com.recipesharing.dto.request.CreateRecipeRequest;
import com.recipesharing.dto.request.UpdateIngredientRequest;
import com.recipesharing.dto.request.UpdateRecipeRequest;
import com.recipesharing.dto.response.IngredientResponse;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.Ingredient;
import com.recipesharing.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RecipeMapper {

    Recipe toRecipe(CreateRecipeRequest request);

    @Mapping(source = "chef.username", target = "chefUsername")
    RecipeResponse toRecipeResponse(Recipe recipe);

    @Mapping(source = "chef.username", target = "chefUsername")
    RecipeListResponse toRecipeListResponse(Recipe recipe);
    
    Ingredient toIngredient(CreateIngredientRequest request);
    
    @Mapping(source = "recipe.id", target = "recipeId")
    IngredientResponse toIngredientResponse(Ingredient ingredient);
    

    List<RecipeListResponse> toRecipeListResponses(List<Recipe> recipes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chef", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateRecipeFromRequest(
            UpdateRecipeRequest request,
            @MappingTarget Recipe recipe
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    void updateIngredientFromRequest(
            UpdateIngredientRequest request,
            @MappingTarget Ingredient ingredient
    );
}
