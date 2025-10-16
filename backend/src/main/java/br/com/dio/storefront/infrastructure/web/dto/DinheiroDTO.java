package br.com.dio.storefront.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para representação de valores monetários.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record DinheiroDTO(
        
        @NotNull(message = "Valor não pode ser null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Valor deve ser >= 0")
        BigDecimal valor,
        
        @NotNull(message = "Moeda não pode ser null")
        String moeda
) {
}
