package br.com.dio.warehouse.domain.event;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.dio.warehouse.domain.valueobject.Money;
import lombok.Builder;
import lombok.Value;

/**
 * Event published when baskets are sold
 * 
 * @author Franklin Canduri
 */
@Value
@Builder
public class BasketsSoldEvent {
    UUID eventId;
    Instant occurredOn;
    Long quantity;
    Money totalValue;
    String transactionId;

    @JsonCreator
    public BasketsSoldEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("occurredOn") Instant occurredOn,
            @JsonProperty("quantity") Long quantity,
            @JsonProperty("totalValue") Money totalValue,
            @JsonProperty("transactionId") String transactionId
    ) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.quantity = quantity;
        this.totalValue = totalValue;
        this.transactionId = transactionId;
    }

    public static BasketsSoldEvent of(Long quantity, Money totalValue, String transactionId) {
        return BasketsSoldEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .quantity(quantity)
            .totalValue(totalValue)
            .transactionId(transactionId)
            .build();
    }
}
