package br.com.dio.warehouse.domain.event;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a new delivery is received
 * 
 * @author Franklin Canduri
 */
@Value
@Builder
public class DeliveryReceivedEvent {
    UUID eventId;
    Instant occurredOn;
    UUID deliveryBoxId;
    Long totalQuantity;

    public static DeliveryReceivedEvent of(UUID deliveryBoxId, Long totalQuantity) {
        return DeliveryReceivedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .deliveryBoxId(deliveryBoxId)
            .totalQuantity(totalQuantity)
            .build();
    }
}
