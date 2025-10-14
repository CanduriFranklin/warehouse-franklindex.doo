package br.com.dio.warehouse.application.service;

import br.com.dio.warehouse.application.port.in.ReceiveDeliveryUseCase;
import br.com.dio.warehouse.application.port.out.EventPublisher;
import br.com.dio.warehouse.domain.event.DeliveryReceivedEvent;
import br.com.dio.warehouse.domain.model.DeliveryBox;
import br.com.dio.warehouse.domain.repository.DeliveryBoxRepository;
import br.com.dio.warehouse.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for receiving deliveries
 * Application layer service that orchestrates domain logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiveDeliveryService implements ReceiveDeliveryUseCase {
    
    private final DeliveryBoxRepository deliveryBoxRepository;
    private final EventPublisher eventPublisher;
    
    @Override
    @Transactional
    public DeliveryBox execute(ReceiveDeliveryCommand command) {
        log.info("Receiving delivery of {} baskets", command.totalQuantity());
        
        // Create delivery box with cost and profit margin
        DeliveryBox deliveryBox = DeliveryBox.builder()
                .totalQuantity(command.totalQuantity())
                .validationDate(command.validationDate())
                .totalCost(Money.of(command.totalCost()))
                .profitMargin(command.profitMarginPercentage().doubleValue())
                .build();
        
        // Calculate selling price
        Money sellingPrice = deliveryBox.calculateSellingPrice(command.profitMarginPercentage().doubleValue());
        
        // Generate individual baskets
        deliveryBox.generateBaskets(sellingPrice);
        
        // Save delivery box (cascade will save baskets)
        DeliveryBox savedDeliveryBox = deliveryBoxRepository.save(deliveryBox);
        
        // Publish domain event using factory method
        DeliveryReceivedEvent event = DeliveryReceivedEvent.of(
                savedDeliveryBox.getId(),
                command.totalQuantity()
        );
        
        eventPublisher.publish(event);
        
        log.info("Delivery received successfully. Delivery ID: {}", savedDeliveryBox.getId());
        
        return savedDeliveryBox;
    }
}
