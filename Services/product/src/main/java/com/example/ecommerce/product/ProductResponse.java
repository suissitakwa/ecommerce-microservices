package com.example.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductResponse(
        Integer Id,

        String name,

        String description,


        double availableQuantity,

        BigDecimal price,

        Integer CategoryId,
        String CategoryName,
        String CategoryDescription
) {
}
