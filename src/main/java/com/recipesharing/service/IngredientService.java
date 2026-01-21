package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.request.CreateIngredientRequest;
import com.recipesharing.dto.request.UpdateIngredientRequest;
import com.recipesharing.dto.response.IngredientResponse;
import com.recipesharing.entity.Ingredient;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.IngredientRepository;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.util.ValidationUti;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;
    private final ValidationUti validationUti;

    public IngredientService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, RecipeMapper recipeMapper, ValidationUti validationUti) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeMapper = recipeMapper;
        this.validationUti = validationUti;
    }

    @Transactional(readOnly = true)
    public List<IngredientResponse> getIngredientsByRecipeId(Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        return recipe.getIngredients()
                .stream()
                .map(recipeMapper::toIngredientResponse)
                .toList();
    }

    @Transactional
    public IngredientResponse addIngredientToRecipe(
            Long recipeId,
            CreateIngredientRequest request,
            User currentUser
    ) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        Ingredient ingredient = recipeMapper.toIngredient(request);
        ingredient.setRecipe(recipe);

        ingredientRepository.save(ingredient);

        return recipeMapper.toIngredientResponse(ingredient);
    }

    @Transactional
    public IngredientResponse updateIngredientInRecipe(
            Long recipeId,
            UpdateIngredientRequest request,
            User currentUser
    ) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        Ingredient ingredient = ingredientRepository
                .findByIdAndRecipeId(request.getId(), recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ingredient not found with id: " + request.getId()
                                        + " for recipe id: " + recipeId
                        )
                );

        recipeMapper.updateIngredientFromRequest(request, ingredient);

        ingredientRepository.save(ingredient);

        return recipeMapper.toIngredientResponse(ingredient);
    }

    @Transactional
    public void deleteIngredientFromRecipe(
            Long recipeId,
            Long ingredientId,
            User currentUser
    ) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        Ingredient ingredient = ingredientRepository
                .findByIdAndRecipeId(ingredientId, recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ingredient not found with id: " + ingredientId
                                        + " for recipe id: " + recipeId
                        )
                );

        ingredientRepository.delete(ingredient);
    }
}

