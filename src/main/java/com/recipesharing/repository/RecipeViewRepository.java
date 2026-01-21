package com.recipesharing.repository;

import com.recipesharing.entity.Rating;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.RecipeView;
import com.recipesharing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeViewRepository extends JpaRepository<RecipeView, Long> {
    Optional<RecipeView> findByUserAndRecipe(User user, Recipe recipe);

    boolean existsByUserAndRecipe(User user, Recipe recipe);

    Page<RecipeView> findAllByRecipeId(Long recipeId, Pageable pageable);
}

