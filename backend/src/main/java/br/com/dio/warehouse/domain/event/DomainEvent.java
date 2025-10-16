package br.com.dio.warehouse.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * Base Domain Event
 * 
 * @author Franklin Canduri
 */
@Value
@Builder
public class DomainEvent {
    UUID eventId;
    String eventType;
    Instant occurredOn;
    Object payload;

    public static DomainEvent of(String eventType, Object payload) {
        return DomainEvent.builder()
            .eventId(UUID.randomUUID())
            .eventType(eventType)
            .occurredOn(Instant.now())
            .payload(payload)
            .build();
    }
}
