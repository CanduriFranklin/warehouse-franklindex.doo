package br.com.dio.warehouse.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for delivery operations
 */
public record DeliveryResponse(
        UUID deliveryId,
        Long totalQuantity,
        
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate validationDate,
        
        BigDecimal totalCost,
        BigDecimal unitCost,
        BigDecimal sellingPrice,
        BigDecimal profitMarginPercentage,
        Long availableBaskets,
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime receivedAt,
        
        String message
) {}
