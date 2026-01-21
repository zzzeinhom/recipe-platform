package com.recipesharing.service;

import com.recipesharing.dto.mapper.RecipeMapper;
import com.recipesharing.dto.request.LabelsListRequest;
import com.recipesharing.dto.response.RecipeListResponse;
import com.recipesharing.dto.response.RecipeResponse;
import com.recipesharing.entity.Label;
import com.recipesharing.entity.Recipe;
import com.recipesharing.entity.User;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.exception.ResourceNotFoundException;
import com.recipesharing.repository.LabelRepository;
import com.recipesharing.repository.RecipeRepository;
import com.recipesharing.util.LabelUtils;
import com.recipesharing.util.ValidationUti;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LabelService {
    private final LabelRepository labelRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final ValidationUti validationUti;
    private final LabelUtils labelUtils;

    public LabelService(LabelRepository labelRepository, RecipeRepository recipeRepository, RecipeMapper recipeMapper, ValidationUti validationUti, LabelUtils labelUtils) {
        this.labelRepository = labelRepository;
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.validationUti = validationUti;
        this.labelUtils = labelUtils;
    }

    @Transactional
    public RecipeResponse addLabelsToRecipe(
            Long recipeId,
            LabelsListRequest labelsListRequest,
            User currentUser
    ) {
        List<String> labelNames = labelsListRequest.getLabels();

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Recipe not found with id: " + recipeId)
                );

        validationUti.validateRecipeOwnership(recipe, currentUser);

        Set<String> normalizedNames = labelNames.stream()
                .map(labelUtils::normalizeLabelName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        if (normalizedNames.isEmpty()) {
            throw new BadRequestException("Labels list cannot be empty");
        }

        List<Label> existingLabels =
                labelRepository.findAllByNameIn(normalizedNames);

        Set<String> existingNames = existingLabels.stream()
                .map(Label::getName)
                .collect(Collectors.toSet());

        Set<Label> newLabels = normalizedNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Label.builder()
                        .name(name)
                        .build())
                .collect(Collectors.toSet());

        if (!newLabels.isEmpty()) {
            labelRepository.saveAll(newLabels);
        }

        Set<Label> allLabels = new HashSet<>();
        allLabels.addAll(existingLabels);
        allLabels.addAll(newLabels);

        recipe.addLabels(allLabels);

        Recipe savedRecipe = recipeRepository.save(recipe);

        return recipeMapper.toRecipeResponse(savedRecipe);
    }

    @Transactional
    public void removeLabelFromRecipe(Long recipeId, Long labelId, User currentUser) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        validationUti.validateRecipeOwnership(recipe, currentUser);

        Label label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));
        recipe.removeLabel(label);

        if (!recipe.getLabels().contains(label)){
            throw new BadRequestException("Label is not associated with the recipe");
        }

        recipeRepository.save(recipe);
    }
}

