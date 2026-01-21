package com.recipesharing.dto.request;

import com.recipesharing.entity.IngredientUnit;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class UpdateIngredientRequest {
    private Long id;

    @Size(min = 1, max = 100, message = "Ingredient name must be between 1 and 100 characters")
    private String name;

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("10000.00")
    private BigDecimal quantity;

    private IngredientUnit unit;

    @Min(0)
    @Max(1000)
    private Integer displayOrder;
}
