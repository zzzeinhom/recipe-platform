package com.recipesharing.util;

import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class ValidationUti {
    public void validateRecipeOwnership(Recipe recipe, User currentUser) {
        if (!recipe.getChef().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not allowed to perform this operation");
        }
    }}

