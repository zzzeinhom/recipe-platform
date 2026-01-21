package com.recipesharing.dto.request;

import com.recipesharing.entity.Ingredient;
import com.recipesharing.entity.IngredientUnit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateIngredientRequest {

    @NotBlank
    @Size(min = 1, max = 100, message = "Ingredient name must be between 1 and 100 characters")
    private String name;

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("10000.00")
    private BigDecimal quantity;

    @NotNull
    private IngredientUnit unit;

    @Min(0)
    @Max(1000)
    private Integer displayOrder;
}

