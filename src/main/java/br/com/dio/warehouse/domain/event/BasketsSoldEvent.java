package br.com.dio.warehouse.domain.event;

import br.com.dio.warehouse.domain.valueobject.Money;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

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
