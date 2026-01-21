package com.recipesharing.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class UpdateRecipeRequest {

    @NotNull
    private Long id;

    @Size(max = 120)
    private String title;

    @Size(max = 600)
    private String description;

    @Size(max = 10000)
    private String instructions;

    @Min(1)
    @Max(1440)
    private Integer prepTime;

    @Max(1440)
    @Min(1)
    private Integer cookTime;

    @Min(1)
    @Max(50)
    private Integer servings;

    private String difficulty;
    private String imageUrl;
    private String thumbnailUrl;}

