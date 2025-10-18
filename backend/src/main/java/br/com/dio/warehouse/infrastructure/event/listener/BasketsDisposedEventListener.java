package br.com.dio.warehouse.infrastructure.event.listener;

import br.com.dio.warehouse.domain.event.BasketsDisposedEvent;
import br.com.dio.warehouse.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Event Listener for BasketsDisposedEvent
 * 
 * Processes baskets disposed events asynchronously.
 * 
 * Responsibilities:
 * - Record loss/waste metrics
 * - Trigger alerts for high disposal rates
 * - Update quality control systems
 * - Generate compliance reports
 * - Optimize inventory management
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
public class BasketsDisposedEventListener {
    
    /**
     * Handles BasketsDisposedEvent from RabbitMQ
     * 
     * @param event The baskets disposed event
     */
    @RabbitListener(queues = RabbitMQConfig.BASKETS_DISPOSED_QUEUE)
    public void handleBasketsDisposed(@Payload BasketsDisposedEvent event) {
        log.info("🗑️ Received BasketsDisposedEvent: quantity={}, lossAmount={}",
                event.getQuantity(),
                event.getLossAmount());
        
        try {
            // Process the event
            processDisposalEvent(event);
            
            log.info("✅ Successfully processed BasketsDisposedEvent: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("❌ Error processing BasketsDisposedEvent: {} - Error: {}",
                    event.getEventId(),
                    e.getMessage(),
                    e);
            
            // Exception will trigger retry mechanism
            throw new RuntimeException("Failed to process BasketsDisposedEvent", e);
        }
    }
    
    /**
     * Business logic for processing disposal event
     * 
     * @param event The disposal event
     */
    private void processDisposalEvent(BasketsDisposedEvent event) {
        
        // Examples:
        // 1. Record loss in accounting
        // accountingService.recordLoss(event.getLossAmount());
        
        // 2. Update waste metrics
        // wasteMetricsService.recordWaste(event.getQuantity(), event.getLossAmount());
        
        // 3. Check if alert threshold exceeded
        // if (event.getQuantity() > ALERT_THRESHOLD) {
        //     alertService.sendHighDisposalAlert(event);
        // }
        
        // 4. Update quality control dashboard
        // qualityDashboardService.updateDisposalMetrics(event);
        
        // 5. Generate compliance report
        // complianceService.recordDisposal(event);
        
        // 6. Optimize inventory parameters
        // inventoryOptimizationService.analyzeWaste(event);
        
        // 7. Log to audit trail
        log.debug("📝 Audit: Baskets disposed - Quantity: {}, Loss: {}, Timestamp: {}",
                event.getQuantity(),
                event.getLossAmount(),
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
