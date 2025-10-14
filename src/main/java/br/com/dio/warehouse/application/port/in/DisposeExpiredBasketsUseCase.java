package br.com.dio.warehouse.application.port.in;

import java.util.List;
import java.util.UUID;

/**
 * Use case for disposing expired basic baskets
 * Input port in hexagonal architecture
 */
public interface DisposeExpiredBasketsUseCase {
    
    /**
     * Disposes all expired baskets in the warehouse
     * 
     * @return The result of the disposal operation
     */
    DisposeExpiredBasketsResult execute();
    
    /**
     * Result of the dispose expired baskets operation
     */
    record DisposeExpiredBasketsResult(
            List<UUID> disposedBasketIds,
            Long totalDisposed,
            String message
    ) {}
}
