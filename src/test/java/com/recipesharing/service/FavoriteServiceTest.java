package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.entity.Favorite;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.repository.FavoriteRepository;
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
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeStatisticsService recipeStatisticsService;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void addFavorite_success() {

        User user = User.builder().id(1L).build();
        Recipe recipe = Recipe.builder().id(5L).build();

        when(recipeRepository.findById(5L))
                .thenReturn(Optional.of(recipe));

        favoriteService.addFavorite(5L, user);

        verify(favoriteRepository).save(any(Favorite.class));
        verify(recipeStatisticsService).increaseFavorites(5L);
    }

    @Test
    void cannotFavoriteTwice() {

        User user = User.builder().id(1L).build();
        Recipe recipe = Recipe.builder().id(5L).build();

        when(recipeRepository.findById(5L))
                .thenReturn(Optional.of(recipe));

        when(favoriteRepository.existsByUserAndRecipe(user, recipe))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> favoriteService.addFavorite(5L, user)
        );
    }
}
