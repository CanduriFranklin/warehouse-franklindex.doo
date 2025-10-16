package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.application.port.in.DisposeExpiredBasketsUseCase;
import br.com.dio.warehouse.application.port.out.EventPublisher;
import br.com.dio.warehouse.domain.event.BasketsDisposedEvent;
import br.com.dio.warehouse.domain.model.BasicBasket;
import br.com.dio.warehouse.domain.repository.BasketRepository;
import br.com.dio.warehouse.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for disposing expired baskets
 * Application layer service that orchestrates domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisposeExpiredBasketsService implements DisposeExpiredBasketsUseCase {
    
    private final BasketRepository basketRepository;
    private final EventPublisher eventPublisher;
    
    @Override
    @Transactional
    public DisposeExpiredBasketsResult execute() {
        log.info("Disposing expired baskets");
        
        // Find all expired baskets
        List<BasicBasket> expiredBaskets = basketRepository.findExpiredBaskets();
        
        if (expiredBaskets.isEmpty()) {
            log.info("No expired baskets found");
            return new DisposeExpiredBasketsResult(
                    List.of(),
                    0L,
                    "No expired baskets to dispose"
            );
        }
        
        // Mark baskets as disposed
        expiredBaskets.forEach(BasicBasket::markAsDisposed);
        
        // Save baskets
        basketRepository.saveAll(expiredBaskets);
        
        // Get basket IDs
        List<UUID> disposedBasketIds = expiredBaskets.stream()
                .map(BasicBasket::getId)
                .toList();
        
        // Calculate total loss amount
        BigDecimal lossAmount = expiredBaskets.stream()
                .map(basket -> basket.getPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Publish domain event using factory method
        BasketsDisposedEvent event = BasketsDisposedEvent.of(
                (long) expiredBaskets.size(),
                Money.of(lossAmount)
        );
        
        eventPublisher.publish(event);
        
        log.info("Successfully disposed {} expired baskets", expiredBaskets.size());
        
        return new DisposeExpiredBasketsResult(
                disposedBasketIds,
                (long) expiredBaskets.size(),
                "Successfully disposed " + expiredBaskets.size() + " expired baskets"
        );
    }
}
