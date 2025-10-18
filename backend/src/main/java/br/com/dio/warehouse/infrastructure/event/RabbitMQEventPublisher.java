package br.com.dio.warehouse.infrastructure.event;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.dio.warehouse.application.port.out.EventPublisher;
import br.com.dio.warehouse.domain.event.BasketsDisposedEvent;
import br.com.dio.warehouse.domain.event.BasketsSoldEvent;
import br.com.dio.warehouse.domain.event.DeliveryReceivedEvent;
import br.com.dio.warehouse.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ implementation of EventPublisher
 * 
 * Publishes domain events to RabbitMQ exchanges with appropriate routing keys.
 * Handles serialization, error handling, and dead letter routing.
 * 
 * Features:
 * - Automatic JSON serialization
 * - Publisher confirms
 * - Return handling for unroutable messages
 * - Dead letter queue support
 * - Structured logging
 * 
 * Event Routing:
 * - DeliveryReceivedEvent → delivery.received
 * - BasketsSoldEvent → baskets.sold
 * - BasketsDisposedEvent → baskets.disposed
 * 
 * Configuration:
 * Enable with: rabbitmq.enabled=true (default: true)
 * Disable for tests or local dev: rabbitmq.enabled=false
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQEventPublisher implements EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public void publish(Object event) {
        if (event == null) {
            log.warn("⚠️ Attempted to publish null event");
            return;
        }
        
        String routingKey = determineRoutingKey(event);
        
        if (routingKey == null) {
            log.warn("⚠️ Unknown event type: {}. Event will not be published.", 
                    event.getClass().getSimpleName());
            return;
        }
        
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    routingKey,
                    event
            );
            
            log.info("📢 Published event: {} to exchange: {} with routing key: {}",
                    event.getClass().getSimpleName(),
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    routingKey);
            
            log.debug("Event details: {}", event);
            
        } catch (AmqpException e) {
            log.error("❌ Failed to publish event: {} - Error: {}",
                    event.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
            
            // Depending on business requirements, you might want to:
            // 1. Throw exception to fail the transaction
            // 2. Store event in database for retry
            // 3. Send to fallback system
            
            // For now, we log and continue (at-most-once delivery)
        }
    }
    
    /**
     * Determines the routing key based on event type
     * 
     * @param event The domain event
     * @return Routing key or null if unknown event type
     */
    private String determineRoutingKey(Object event) {
        return switch (event) {
            case DeliveryReceivedEvent _ -> RabbitMQConfig.DELIVERY_ROUTING_KEY;
            case BasketsSoldEvent _ -> RabbitMQConfig.BASKETS_SOLD_ROUTING_KEY;
            case BasketsDisposedEvent _ -> RabbitMQConfig.BASKETS_DISPOSED_ROUTING_KEY;
            default -> {
                log.warn("⚠️ Unknown event type: {}", event.getClass().getName());
                yield null;
            }
        };
    }
    
    @Override
    public void publishAll(Object... events) {
        if (events == null || events.length == 0) {
            log.debug("No events to publish");
            return;
        }
        
        log.info("📢 Publishing {} events in batch", events.length);
        
        int successCount = 0;
        int failureCount = 0;
        
        for (Object event : events) {
            try {
                publish(event);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("❌ Failed to publish event in batch: {}", 
                        event != null ? event.getClass().getSimpleName() : "null", e);
            }
        }
        
        log.info("📊 Batch publish complete: {} successful, {} failed", successCount, failureCount);
    }
}
