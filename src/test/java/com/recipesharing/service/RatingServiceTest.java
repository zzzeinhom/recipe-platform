package com.recipesharing.service;

import com.recipesharing.dto.mapper.RatingMapper;
import com.recipesharing.dto.request.CreateRatingRequest;
import com.recipesharing.entity.Rating;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.repository.RatingRepository;
import com.recipesharing.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private RecipeStatisticsService recipeStatisticsService;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void cannotRateOwnRecipe() {

        User chef = User.builder().id(1L).build();
        Recipe recipe = Recipe.builder().chef(chef).build();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        assertThrows(
                BadRequestException.class,
                () -> ratingService.addRating(1L, new CreateRatingRequest(), chef)
        );
    }

    @Test
    void cannotRateTwice() {

        User user = User.builder().id(2L).build();
        Recipe recipe = Recipe.builder()
                .chef(User.builder().id(1L).build())
                .build();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));

        when(ratingRepository.existsByUserAndRecipe(user, recipe))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ratingService.addRating(1L, new CreateRatingRequest(), user)
        );
    }

    @Test
    void ratingSavedSuccessfully() {

        User user = User.builder().id(2L).build();
        Recipe recipe = Recipe.builder()
                .chef(User.builder().id(1L).build())
                .build();

        Rating rating = new Rating();

        when(recipeRepository.findById(1L))
                .thenReturn(Optional.of(recipe));
        when(ratingMapper.toRating(any()))
                .thenReturn(rating);

        ratingService.addRating(1L, new CreateRatingRequest(), user);

        verify(ratingRepository).save(any(Rating.class));
        verify(recipeStatisticsService).updateRatingStats(1L);
    }
}
