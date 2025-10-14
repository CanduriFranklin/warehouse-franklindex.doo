package br.com.dio.warehouse.adapter.in.web.dto;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for sell baskets operation
 */
public record SellBasketsResponse(
        List<UUID> soldBasketIds,
        Long totalSold,
        String message
) {}
