package br.com.dio.warehouse.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public DeliveryReceivedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("occurredOn") Instant occurredOn,
            @JsonProperty("deliveryBoxId") UUID deliveryBoxId,
            @JsonProperty("totalQuantity") Long totalQuantity
    ) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.deliveryBoxId = deliveryBoxId;
        this.totalQuantity = totalQuantity;
    }

    public static DeliveryReceivedEvent of(UUID deliveryBoxId, Long totalQuantity) {
        return DeliveryReceivedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .deliveryBoxId(deliveryBoxId)
            .totalQuantity(totalQuantity)
            .build();
    }
}
