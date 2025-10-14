package br.com.dio.warehouse.infrastructure.event.listener;

import br.com.dio.warehouse.domain.event.DeliveryReceivedEvent;
import br.com.dio.warehouse.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Event Listener for DeliveryReceivedEvent
 * 
 * Processes delivery received events asynchronously.
 * 
 * Responsibilities:
 * - Log delivery information
 * - Trigger notifications (email, SMS, etc.)
 * - Update analytics/metrics
 * - Integrate with external systems
 * 
 * Error Handling:
 * - Automatic retry with exponential backoff (configured in RabbitMQConfig)
 * - After max retries, message goes to Dead Letter Queue
 * - Failed messages can be manually inspected and reprocessed
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class DeliveryReceivedEventListener {
    
    /**
     * Handles DeliveryReceivedEvent from RabbitMQ
     * 
     * @param event The delivery received event
     */
    @RabbitListener(queues = RabbitMQConfig.DELIVERY_QUEUE)
    public void handleDeliveryReceived(@Payload DeliveryReceivedEvent event) {
        log.info("📦 Received DeliveryReceivedEvent: deliveryBoxId={}, quantity={}",
                event.getDeliveryBoxId(),
                event.getTotalQuantity());
        
        try {
            // Process the event
            processDeliveryEvent(event);
            
            log.info("✅ Successfully processed DeliveryReceivedEvent: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("❌ Error processing DeliveryReceivedEvent: {} - Error: {}",
                    event.getEventId(),
                    e.getMessage(),
                    e);
            
            // Exception will trigger retry mechanism
            throw new RuntimeException("Failed to process DeliveryReceivedEvent", e);
        }
    }
    
    /**
     * Business logic for processing delivery event
     * 
     * @param event The delivery event
     */
    private void processDeliveryEvent(DeliveryReceivedEvent event) {
        // TODO: Implement actual business logic
        
        // Examples:
        // 1. Send notification to warehouse manager
        // notificationService.sendDeliveryNotification(event);
        
        // 2. Update inventory metrics
        // metricsService.recordDelivery(event.getTotalQuantity());
        
        // 3. Integrate with ERP system
        // erpIntegrationService.notifyDelivery(event);
        
        // 4. Log to audit trail
        log.debug("📝 Audit: Delivery received - DeliveryBoxId: {}, Quantity: {}, Timestamp: {}",
                event.getDeliveryBoxId(),
                event.getTotalQuantity(),
                event.getOccurredOn());
        
        // Simulate processing time
        simulateProcessing();
    }
    
    /**
     * Simulates processing time (remove in production)
     */
    private void simulateProcessing() {
        try {
            Thread.sleep(100); // 100ms processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
