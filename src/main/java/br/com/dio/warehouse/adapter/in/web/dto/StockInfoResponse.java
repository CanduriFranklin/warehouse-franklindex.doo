package br.com.dio.warehouse.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Response DTO for stock information
 */
public record StockInfoResponse(
        Long totalBaskets,
        Long availableBaskets,
        Long soldBaskets,
        Long disposedBaskets,
        Long expiredBaskets,
        BigDecimal totalInventoryValue
) {}
