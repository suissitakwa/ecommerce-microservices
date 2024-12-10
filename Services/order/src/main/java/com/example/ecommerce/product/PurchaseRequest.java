package com.example.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message="product id is mandatory")
        Integer productId,
        @Positive(message="quatity is mandatory")
        double quantity

) {
}
