package br.com.dio.warehouse.infrastructure.event;

import br.com.dio.warehouse.application.port.out.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Simple logging implementation of EventPublisher
 * 
 * This is a fallback implementation used when RabbitMQ is disabled.
 * Useful for:
 * - Local development without RabbitMQ
 * - Unit tests
 * - Debugging
 * 
 * Enable with: rabbitmq.enabled=false
 * 
 * In production, use RabbitMQEventPublisher instead.
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "rabbitmq.enabled", havingValue = "false")
public class LoggingEventPublisher implements EventPublisher {
    
    public LoggingEventPublisher() {
        log.warn("⚠️ Using LoggingEventPublisher - RabbitMQ is DISABLED");
        log.warn("⚠️ Events will only be logged, not published to message broker");
    }
    
    @Override
    public void publish(Object event) {
        log.info("📢 Domain Event Published (LOG ONLY): {}", event.getClass().getSimpleName());
        log.debug("Event details: {}", event);
    }
    
    @Override
    public void publishAll(Object... events) {
        if (events == null || events.length == 0) {
            return;
        }
        
        log.info("📢 Publishing {} events in batch (LOG ONLY)", events.length);
        for (Object event : events) {
            publish(event);
        }
    }
}
