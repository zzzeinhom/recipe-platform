package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.entity.Favorite;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.FavoriteRepository;
import com.recipesharing.repository.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final RecipeStatisticsService recipeStatisticsService;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            RecipeRepository recipeRepository,
            RecipeMapper recipeMapper,
            RecipeStatisticsService recipeStatisticsService
    ) {
        this.favoriteRepository = favoriteRepository;
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.recipeStatisticsService = recipeStatisticsService;
    }

    // ============================
    // ADD FAVORITE
    // ============================

    @Transactional
    public void addFavorite(Long recipeId, User user) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        if (favoriteRepository.existsByUserAndRecipe(user, recipe)) {
            throw new BadRequestException("Recipe already added to favorites");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);

        favoriteRepository.save(favorite);

        recipeStatisticsService.increaseFavorites(recipeId);
    }

    // ============================
    // REMOVE FAVORITE
    // ============================

    @Transactional
    public void removeFavorite(Long recipeId, User user) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        Favorite favorite = favoriteRepository.findByUserAndRecipe(user, recipe)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Favorite not found")
                );

        favoriteRepository.delete(favorite);

        recipeStatisticsService.decreaseFavorites(recipeId);
    }

    // ============================
    // GET USER FAVORITES
    // ============================

    @Transactional(readOnly = true)
    public Page<RecipeListResponse> getUserFavorites(User user, Pageable pageable) {

        Page<Favorite> favoritesPage =
                favoriteRepository.findAllByUser(user, pageable);

        return favoritesPage.map(
                favorite -> recipeMapper.toRecipeListResponse(favorite.getRecipe())
        );
    }
}
