package br.com.dio.warehouse.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Response DTO for cash register information
 */
public record CashRegisterResponse(
        BigDecimal totalRevenue,
        BigDecimal totalCost,
        BigDecimal grossProfit,
        BigDecimal profitMargin,
        Long totalBasketsSold
) {}
