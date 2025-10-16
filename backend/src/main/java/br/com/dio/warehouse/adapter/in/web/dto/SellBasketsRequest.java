package br.com.dio.warehouse.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for selling baskets
 */
public record SellBasketsRequest(
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Long quantity
) {}
