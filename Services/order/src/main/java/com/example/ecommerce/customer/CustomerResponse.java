package com.example.ecommerce.customer;

public record CustomerResponse(
    String Id,
    String firstname,
    String lastname,
    String email
) {
}
