package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.application.port.in.GetCashRegisterUseCase;
import br.com.dio.warehouse.domain.model.BasicBasket;
import br.com.dio.warehouse.domain.model.DeliveryBox;
import br.com.dio.warehouse.domain.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Service implementation for getting cash register information
 * Application layer service that orchestrates domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCashRegisterService implements GetCashRegisterUseCase {
    
    private final BasketRepository basketRepository;
    
    @Override
    @Transactional(readOnly = true)
    public CashRegisterInfo execute() {
        log.debug("Getting cash register information");
        
        // Get all sold baskets
        List<BasicBasket> soldBaskets = basketRepository.findByStatus(BasicBasket.BasketStatus.SOLD);
        
        // Calculate total revenue (sum of prices of sold baskets)
        BigDecimal totalRevenue = soldBaskets.stream()
                .map(basket -> basket.getPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total cost (sum of unit costs from delivery boxes)
        BigDecimal totalCost = soldBaskets.stream()
                .map(basket -> {
                    DeliveryBox delivery = basket.getDeliveryBox();
                    return delivery != null ? delivery.getUnitCost().getAmount() : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate gross profit
        BigDecimal grossProfit = totalRevenue.subtract(totalCost);
        
        // Calculate profit margin percentage
        BigDecimal profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        
        long totalBasketsSold = soldBaskets.size();
        
        log.debug("Cash register - Revenue: {}, Cost: {}, Profit: {}, Margin: {}%, Baskets sold: {}",
                totalRevenue, totalCost, grossProfit, profitMargin, totalBasketsSold);
        
        return new CashRegisterInfo(
                totalRevenue,
                totalCost,
                grossProfit,
                profitMargin,
                totalBasketsSold
        );
    }
}
