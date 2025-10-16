package br.com.dio.warehouse.adapter.in.web.dto;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for dispose expired baskets operation
 */
public record DisposeExpiredBasketsResponse(
        List<UUID> disposedBasketIds,
        Long totalDisposed,
        String message
) {}
