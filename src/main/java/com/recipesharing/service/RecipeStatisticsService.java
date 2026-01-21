package com.recipesharing.service;

import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.RecipeView;
import com.recipesharing.entity.User;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.repository.RecipeViewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RecipeStatisticsService {

    private final RecipeRepository recipeRepository;

    public RecipeStatisticsService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // --------------------
    // Views
    // --------------------

    @Transactional
    public void incrementViews(Long recipeId) {
        recipeRepository.increaseViewCount(recipeId);
    }

    // --------------------
    // Favorites
    // --------------------

    @Transactional
    public void increaseFavorites(Long recipeId) {
        recipeRepository.increaseFavoritesCount(recipeId);
    }

    @Transactional
    public void decreaseFavorites(Long recipeId) {
        recipeRepository.decreaseFavoritesCount(recipeId);
    }

    // --------------------
    // Ratings
    // --------------------

    @Transactional
    public void updateRatingStats(Long recipeId) {
        Double avg = recipeRepository.findAverageRatingById(recipeId);
        Long count = recipeRepository.countRatingsById(recipeId);

        recipeRepository.updateRatingStats(
                recipeId,
                avg != null ? avg : 0.0,
                count != null ? count : 0
        );
    }
}
