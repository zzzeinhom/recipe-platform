package com.recipesharing.dto.request;

import com.recipesharing.entity.RecipeDifficulty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class CreateRecipeRequest {

    @NotBlank
    @Size(max = 120)
    private String title;

    @Size(max = 600)
    private String description;

    @NotBlank
    @Size(max = 10000)
    private String instructions;

    @NotNull
    @Min(1)
    @Max(1440)
    private Integer prepTime;

    @NotNull
    @Max(1440)
    @Min(1)
    private Integer cookTime;

    @NotNull
    @Min(1)
    @Max(50)
    private Integer servings;

    @NotNull
    private RecipeDifficulty difficulty;

    private String imageUrl;
    private String thumbnailUrl;
}