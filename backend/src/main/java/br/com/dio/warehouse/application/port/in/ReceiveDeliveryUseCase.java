package br.com.dio.warehouse.application.port.in;

import br.com.dio.warehouse.domain.model.DeliveryBox;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Use case for receiving a delivery of basic baskets
 * Input port in hexagonal architecture
 */
public interface ReceiveDeliveryUseCase {
    
    /**
     * Receives a new delivery of basic baskets
     * 
     * @param command The command containing delivery information
     * @return The created delivery box
     */
    DeliveryBox execute(ReceiveDeliveryCommand command);
    
    /**
     * Command for receiving a delivery
     */
    record ReceiveDeliveryCommand(
            Long totalQuantity,
            LocalDate validationDate,
            BigDecimal totalCost,
            BigDecimal profitMarginPercentage
    ) {
        public ReceiveDeliveryCommand {
            if (totalQuantity == null || totalQuantity <= 0) {
                throw new IllegalArgumentException("Total quantity must be positive");
            }
            if (validationDate == null) {
                throw new IllegalArgumentException("Validation date is required");
            }
            if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Total cost must be positive");
            }
            if (profitMarginPercentage == null || profitMarginPercentage.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Profit margin must be non-negative");
            }
        }
    }
}
