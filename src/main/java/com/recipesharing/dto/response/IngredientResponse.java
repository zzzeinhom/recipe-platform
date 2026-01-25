package com.recipesharing.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class IngredientResponse {
    private Long id;
    private String name;
    private BigDecimal quantity;
    private String unit;
}
