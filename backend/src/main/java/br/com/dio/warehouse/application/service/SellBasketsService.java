package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.application.port.in.SellBasketsUseCase;
import br.com.dio.warehouse.application.port.out.EventPublisher;
import br.com.dio.warehouse.domain.event.BasketsSoldEvent;
import br.com.dio.warehouse.domain.exception.InsufficientStockException;
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
 * Service implementation for selling baskets
 * Application layer service that orchestrates domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellBasketsService implements SellBasketsUseCase {
    
    private final BasketRepository basketRepository;
    private final EventPublisher eventPublisher;
    
    @Override
    @Transactional
    public SellBasketsResult execute(SellBasketsCommand command) {
        log.info("Selling {} baskets", command.quantity());
        
        // Get available baskets
        List<BasicBasket> availableBaskets = basketRepository.findAvailableBaskets();
        
        if (availableBaskets.size() < command.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + availableBaskets.size() + 
                    ", Requested: " + command.quantity()
            );
        }
        
        // Select baskets to sell (oldest first - FIFO)
        List<BasicBasket> basketsToSell = availableBaskets.stream()
                .limit(command.quantity())
                .toList();
        
        // Mark baskets as sold
        basketsToSell.forEach(BasicBasket::markAsSold);
        
        // Save baskets
        basketRepository.saveAll(basketsToSell);
        
        // Calculate total revenue
        BigDecimal totalRevenue = basketsToSell.stream()
                .map(basket -> basket.getPrice().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get basket IDs
        List<UUID> soldBasketIds = basketsToSell.stream()
                .map(BasicBasket::getId)
                .toList();
        
        // Publish domain event using factory method
        BasketsSoldEvent event = BasketsSoldEvent.of(
                command.quantity(),
                Money.of(totalRevenue),
                UUID.randomUUID().toString() // Transaction ID
        );
        
        eventPublisher.publish(event);
        
        log.info("Successfully sold {} baskets. Total revenue: {}", command.quantity(), totalRevenue);
        
        return new SellBasketsResult(
                soldBasketIds,
                command.quantity(),
                "Successfully sold " + command.quantity() + " baskets"
        );
    }
}
