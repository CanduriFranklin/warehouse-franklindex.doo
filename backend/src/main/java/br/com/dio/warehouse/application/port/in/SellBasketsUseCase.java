package br.com.dio.warehouse.application.port.in;

import java.util.List;
import java.util.UUID;

/**
 * Use case for selling basic baskets
 * Input port in hexagonal architecture
 */
public interface SellBasketsUseCase {
    
    /**
     * Sells a specified number of baskets
     * 
     * @param command The command containing sale information
     * @return The result of the sale operation
     */
    SellBasketsResult execute(SellBasketsCommand command);
    
    /**
     * Command for selling baskets
     */
    record SellBasketsCommand(
            Long quantity
    ) {
        public SellBasketsCommand {
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }
    
    /**
     * Result of the sell baskets operation
     */
    record SellBasketsResult(
            List<UUID> soldBasketIds,
            Long totalSold,
            String message
    ) {}
}
