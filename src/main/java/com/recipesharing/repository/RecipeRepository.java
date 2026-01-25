package com.recipesharing.repository;

import com.recipesharing.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    @Query("""
    select avg(rating.rating)
    from Rating rating
    where rating.recipe.id = :recipeId
""")
    Double findAverageRatingById(Long recipeId);

    Long countRatingsById(Long recipeId);

    @Modifying
    @Query("""
    update Recipe r
    set r.viewCount = coalesce(r.viewCount, 0) + 1
    where r.id = :id
""")
    void increaseViewCount(Long id);


    @Modifying
    @Query("""
    update Recipe r
    set r.favoritesCount = coalesce(r.favoritesCount, 0) + 1
    where r.id = :id
""")
    void increaseFavoritesCount(Long id);


    @Modifying
    @Query("""
    update Recipe r
    set r.favoritesCount =
        case
            when r.favoritesCount > 0 then r.favoritesCount - 1
            else 0
        end
    where r.id = :id
""")
    void decreaseFavoritesCount(Long id);

    @Modifying
    @Query("""
    update Recipe r
    set r.averageRating = :avg,
        r.ratingCount = :count
    where r.id = :id
""")
    void updateRatingStats(Long id, Double avg, Long count);

}
