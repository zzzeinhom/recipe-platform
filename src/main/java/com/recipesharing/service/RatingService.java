package com.recipesharing.service;

import com.recipesharing.dto.mapper.RatingMapper;
import com.recipesharing.dto.request.CreateRatingRequest;
import com.recipesharing.dto.request.UpdateRatingRequest;
import com.recipesharing.dto.response.RatingResponse;
import com.recipesharing.entity.Rating;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.RatingRepository;
import com.recipesharing.repository.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final RecipeStatisticsService recipeStatisticsService;
    private final RecipeRepository recipeRepository;
    private final RatingMapper ratingMapper;

    public RatingService(RatingRepository ratingRepository, RecipeStatisticsService recipeStatisticsService, RecipeRepository recipeRepository, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.recipeStatisticsService = recipeStatisticsService;
        this.recipeRepository = recipeRepository;
        this.ratingMapper = ratingMapper;
    }

    @Transactional
    public RatingResponse addRating(Long recipeId, CreateRatingRequest request, User user) {

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + recipeId)
        );

        if (recipe.getChef().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot rate your own recipe");
        }

        if (ratingRepository.existsByUserAndRecipe(user, recipe)) {
            throw new BadRequestException("User has already rated this recipe");
        }

        Rating rating = ratingMapper.toRating(request);
        rating.setUser(user);
        rating.setRecipe(recipe);

        Rating saved = ratingRepository.save(rating);

        recipeStatisticsService.updateRatingStats(recipeId);

        return ratingMapper.toRatingResponse(saved);
    }

    @Transactional
    public RatingResponse updateRating(
            Long recipeId,
            UpdateRatingRequest request,
            User user
    ) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + recipeId)
        );

        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe).orElseThrow(
                () -> new ResourceNotFoundException("Rating not found for user and recipe")
        );

        ratingMapper.updateRatingFromRequest(request, rating);

        recipeStatisticsService.updateRatingStats(recipeId);

        return ratingMapper.toRatingResponse(ratingRepository.save(rating));
    }

    @Transactional
    public void deleteRating(
            Long recipeId,
            User user
    ) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + recipeId)
        );

        Rating rating = ratingRepository.findByUserAndRecipe(user, recipe).orElseThrow(
                () -> new ResourceNotFoundException("Rating not found for user and recipe")
        );

        ratingRepository.delete(rating);

        recipeStatisticsService.updateRatingStats(recipeId);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> getRatingsByRecipe(
            Long recipeId,
            Pageable pageable
    ) {
        Page<Rating> ratings = ratingRepository.findAllByRecipeId(recipeId, pageable);
        return ratings.map(ratingMapper::toRatingResponse);
    }
}
