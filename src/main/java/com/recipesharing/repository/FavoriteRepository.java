package com.recipesharing.repository;

import com.recipesharing.entity.Favorite;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>
{
    Optional<Favorite> findByUserAndRecipe(User user, Recipe recipe);

    boolean existsByUserAndRecipe(User user, Recipe recipe);

    Page<Favorite> findAllByUser(User user, Pageable pageable);
}

