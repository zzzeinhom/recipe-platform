package com.recipesharing.repository.specification;

import com.recipesharing.entity.Label;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.RecipeDifficulty;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecification {
    public static Specification<Recipe> hasKeyword(String keyword) {
        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Recipe> hasChef(Long chefId) {
        return (root, query, cb) -> {

            if (chefId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("chef").get("id"), chefId);
        };
    }

    public static Specification<Recipe> hasDifficulty(RecipeDifficulty difficulty) {
        return (root, query, cb) -> {

            if (difficulty == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("difficulty"), difficulty);
        };
    }

    public static Specification<Recipe> hasLabels(List<String> labels) {
        return (root, query, cb) -> {

            if (labels == null || labels.isEmpty()) {
                return cb.conjunction();
            }

            assert query != null;
            query.distinct(true);

            Join<Recipe, Label> labelJoin = root.join("labels");

            return labelJoin.get("name").in(labels);
        };
    }

}
