package br.com.dio.warehouse.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for receiving a delivery
 */
public record ReceiveDeliveryRequest(
        
        @NotNull(message = "Total quantity is required")
        @Positive(message = "Total quantity must be positive")
        Long totalQuantity,
        
        @NotNull(message = "Validation date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate validationDate,
        
        @NotNull(message = "Total cost is required")
        @DecimalMin(value = "0.01", message = "Total cost must be positive")
        BigDecimal totalCost,
        
        @NotNull(message = "Profit margin percentage is required")
        @DecimalMin(value = "0.0", message = "Profit margin must be non-negative")
        BigDecimal profitMarginPercentage
) {}
