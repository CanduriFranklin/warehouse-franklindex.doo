package br.com.dio.warehouse.application.port.in;

import java.math.BigDecimal;

/**
 * Use case for checking warehouse stock information
 * Input port in hexagonal architecture
 */
public interface CheckStockUseCase {
    
    /**
     * Gets current stock information
     * 
     * @return The stock information
     */
    StockInfo execute();
    
    /**
     * Stock information result
     */
    record StockInfo(
            Long totalBaskets,
            Long availableBaskets,
            Long soldBaskets,
            Long disposedBaskets,
            Long expiredBaskets,
            BigDecimal totalInventoryValue
    ) {}
}
