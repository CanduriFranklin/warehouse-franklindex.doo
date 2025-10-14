package br.com.dio.warehouse.infrastructure.event;

import br.com.dio.warehouse.application.port.out.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Simple logging implementation of EventPublisher
 * This is a temporary implementation for development
 * In production, this should be replaced with RabbitMQ or Kafka integration
 */
@Slf4j
@Component
public class LoggingEventPublisher implements EventPublisher {
    
    @Override
    public void publish(Object event) {
        log.info("📢 Domain Event Published: {}", event.getClass().getSimpleName());
        log.debug("Event details: {}", event);
    }
}
