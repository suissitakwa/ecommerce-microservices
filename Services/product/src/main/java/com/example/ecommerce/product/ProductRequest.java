package com.example.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

         Integer Id,
         @NotNull(message = "Product name is required")
         String name,
         @NotNull(message = "Product description is required")
         String description,
         @Positive(message = "Available Quantity should be positive")

         double availableQuantity,
         @Positive(message = "Price should be positive")
         BigDecimal price,
         @NotNull(message = "Product Category is required")
         Integer CategoryId
) {
}
