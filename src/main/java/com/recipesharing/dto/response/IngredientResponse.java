package com.recipesharing.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class IngredientResponse {
    private Long id;
    private Long recipeId;
    private String name;
    private String quantity;
    private BigDecimal price;
    private String unit;
}
