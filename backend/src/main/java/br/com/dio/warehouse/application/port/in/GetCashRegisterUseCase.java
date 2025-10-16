package br.com.dio.warehouse.application.port.in;

import java.math.BigDecimal;

/**
 * Use case for getting cash register information
 * Input port in hexagonal architecture
 */
public interface GetCashRegisterUseCase {
    
    /**
     * Gets current cash register information
     * 
     * @return The cash register information
     */
    CashRegisterInfo execute();
    
    /**
     * Cash register information result
     */
    record CashRegisterInfo(
            BigDecimal totalRevenue,
            BigDecimal totalCost,
            BigDecimal grossProfit,
            BigDecimal profitMargin,
            Long totalBasketsSold
    ) {}
}
