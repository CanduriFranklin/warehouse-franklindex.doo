package br.com.dio.warehouse.infrastructure.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.dio.warehouse.domain.event.BasketsSoldEvent;
import br.com.dio.warehouse.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Event Listener for BasketsSoldEvent
 * 
 * Processes baskets sold events asynchronously.
 * 
 * Responsibilities:
 * - Update sales analytics
 * - Trigger revenue reports
 * - Send customer receipts
 * - Update loyalty programs
 * - Integrate with accounting systems
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
public class BasketsSoldEventListener {
    
    /**
     * Handles BasketsSoldEvent from RabbitMQ
     * 
     * @param event The baskets sold event
     */
    @RabbitListener(queues = RabbitMQConfig.BASKETS_SOLD_QUEUE)
    public void handleBasketsSold(@Payload BasketsSoldEvent event) {
        log.info("💰 Received BasketsSoldEvent: quantity={}, totalValue={}, transactionId={}",
                event.getQuantity(),
                event.getTotalValue(),
                event.getTransactionId());
        
        try {
            // Process the event
            processSalesEvent(event);
            
            log.info("✅ Successfully processed BasketsSoldEvent: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("❌ Error processing BasketsSoldEvent: {} - Error: {}",
                    event.getEventId(),
                    e.getMessage(),
                    e);
            
            // Exception will trigger retry mechanism
            throw new RuntimeException("Failed to process BasketsSoldEvent", e);
        }
    }
    
    /**
     * Business logic for processing sales event
     * 
     * @param event The sales event
     */
    private void processSalesEvent(BasketsSoldEvent event) {
        
        // Examples:
        // 1. Update sales dashboard
        // dashboardService.updateSalesMetrics(event);
        
        // 2. Send receipt to customer
        // receiptService.sendReceipt(event.getTransactionId(), event.getTotalValue());
        
        // 3. Update accounting system
        // accountingService.recordRevenue(event.getTotalValue());
        
        // 4. Trigger loyalty points
        // loyaltyService.addPoints(event.getQuantity());
        
        // 5. Update inventory forecasting
        // forecastingService.recordSale(event.getQuantity());
        
        // 6. Log to audit trail
        log.debug("📝 Audit: Baskets sold - Quantity: {}, Revenue: {}, Transaction: {}, Timestamp: {}",
                event.getQuantity(),
                event.getTotalValue(),
                event.getTransactionId(),
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
