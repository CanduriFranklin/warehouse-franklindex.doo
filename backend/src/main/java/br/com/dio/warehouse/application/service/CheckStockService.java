package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.application.port.in.CheckStockUseCase;
import br.com.dio.warehouse.domain.model.BasicBasket;
import br.com.dio.warehouse.domain.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation for checking stock
 * Application layer service that orchestrates domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckStockService implements CheckStockUseCase {
    
    private final BasketRepository basketRepository;
    
    @Override
    @Transactional(readOnly = true)
    public StockInfo execute() {
        log.debug("Checking stock information");
        
        // Get all baskets
        List<BasicBasket> allBaskets = basketRepository.findAll();
        
        // Count by status
        long totalBaskets = allBaskets.size();
        long availableBaskets = basketRepository.countByStatus(BasicBasket.BasketStatus.AVAILABLE);
        long soldBaskets = basketRepository.countByStatus(BasicBasket.BasketStatus.SOLD);
        long disposedBaskets = basketRepository.countByStatus(BasicBasket.BasketStatus.DISPOSED);
        
        // Find expired baskets (available but past validation date)
        long expiredBaskets = basketRepository.findExpiredBaskets().size();
        
        // Calculate total inventory value (only available baskets)
        BigDecimal totalInventoryValue = allBaskets.stream()
                .filter(basket -> basket.getStatus() == BasicBasket.BasketStatus.AVAILABLE)
                .map(basket -> basket.getPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        log.debug("Stock info - Total: {}, Available: {}, Sold: {}, Disposed: {}, Expired: {}",
                totalBaskets, availableBaskets, soldBaskets, disposedBaskets, expiredBaskets);
        
        return new StockInfo(
                totalBaskets,
                availableBaskets,
                soldBaskets,
                disposedBaskets,
                expiredBaskets,
                totalInventoryValue
        );
    }
}
