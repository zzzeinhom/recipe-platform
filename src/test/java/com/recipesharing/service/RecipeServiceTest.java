package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.request.CreateRecipeRequest;
import com.recipesharing.dto.request.UpdateRecipeRequest;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.util.ValidationUti;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private ValidationUti validationUti;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createRecipe_success() {

        User chef = User.builder().id(1L).build();
        CreateRecipeRequest request = new CreateRecipeRequest();

        Recipe recipe = new Recipe();
        Recipe saved = new Recipe();
        RecipeResponse response = new RecipeResponse();

        when(recipeMapper.toRecipe(request)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(saved);
        when(recipeMapper.toRecipeResponse(saved)).thenReturn(response);

        RecipeResponse result = recipeService.createRecipe(request, chef);

        assertNotNull(result);
        verify(recipeRepository).save(recipe);
    }

    @Test
    void getRecipeById_notFound() {

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> recipeService.getRecipeById(1L, null)
        );
    }

    @Test
    void updateRecipe_ownershipValidated() {

        User chef = User.builder().id(1L).build();
        UpdateRecipeRequest request = new UpdateRecipeRequest();
        request.setId(10L);

        Recipe recipe = Recipe.builder().chef(chef).build();

        when(recipeRepository.findById(10L))
                .thenReturn(Optional.of(recipe));

        recipeService.updateRecipe(request, chef);

        verify(validationUti).validateRecipeOwnership(recipe, chef);
    }
}
