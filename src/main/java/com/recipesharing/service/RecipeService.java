package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.request.CreateRecipeRequest;
import com.recipesharing.dto.request.UpdateRecipeRequest;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.RecipeDifficulty;
import com.recipesharing.entity.RecipeView;
import com.recipesharing.entity.User;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.exception.UnauthorizedException;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.repository.RecipeViewRepository;
import com.recipesharing.repository.specification.RecipeSpecification;
import com.recipesharing.util.ValidationUti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeViewRepository recipeViewRepository;
    private final RecipeMapper recipeMapper;
    private final ValidationUti validationUti;
    private final RecipeStatisticsService recipeStatisticsService;

    public RecipeService(
            RecipeRepository recipeRepository, RecipeViewRepository recipeViewRepository,
            RecipeMapper recipeMapper,
            ValidationUti validationUti, RecipeStatisticsService recipeStatisticsService
    ) {
        this.recipeRepository = recipeRepository;
        this.recipeViewRepository = recipeViewRepository;
        this.recipeMapper = recipeMapper;
        this.validationUti = validationUti;
        this.recipeStatisticsService = recipeStatisticsService;
    }

    // =====================================================
    // CREATE
    // =====================================================

    @Transactional
    public RecipeResponse createRecipe(CreateRecipeRequest request, User currentUser) {
        Recipe recipe = recipeMapper.toRecipe(request);
        recipe.setChef(currentUser);

        Recipe savedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(savedRecipe);
    }

    // =====================================================
    // READ
    // =====================================================

    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long id, User currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + id)
                );

        if (currentUser == null) {
            throw new UnauthorizedException("You are not allowed to access this resource");
        }
        if (!recipeViewRepository.existsByUserAndRecipe(currentUser, recipe)) {
            RecipeView recipeView = new RecipeView();
            recipeView.setUser(currentUser);
            recipeView.setRecipe(recipe);
            recipeViewRepository.save(recipeView);
            recipeStatisticsService.incrementViews(id);
        }

        return recipeMapper.toRecipeResponse(recipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeListResponse> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable)
                .map(recipeMapper::toRecipeListResponse);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Transactional
    public RecipeResponse updateRecipe(UpdateRecipeRequest request, User currentUser) {
        Recipe recipe = recipeRepository.findById(request.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Recipe not found with id: " + request.getId()
                        )
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        recipeMapper.updateRecipeFromRequest(request, recipe);

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return recipeMapper.toRecipeResponse(updatedRecipe);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Transactional
    public void deleteRecipe(Long id, User currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + id)
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        recipeRepository.delete(recipe);
    }

    // =====================================================
    // SEARCH & FILTERING (Phase 4)
    // =====================================================

    @Transactional(readOnly = true)
    public Page<RecipeListResponse> searchRecipes(
            String keyword,
            List<String> labels,
            Long chefId,
            RecipeDifficulty difficulty,
            Pageable pageable
    ) {

        Specification<Recipe> specification = Specification.allOf(
                RecipeSpecification.hasKeyword(keyword),
                RecipeSpecification.hasLabels(labels),
                RecipeSpecification.hasChef(chefId),
                RecipeSpecification.hasDifficulty(difficulty)
        );

        return recipeRepository.findAll(specification, pageable)
                .map(recipeMapper::toRecipeListResponse);
    }
}