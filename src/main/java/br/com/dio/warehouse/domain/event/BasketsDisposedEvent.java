package br.com.dio.warehouse.domain.event;

import br.com.dio.warehouse.domain.valueobject.Money;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when expired baskets are disposed
 * 
 * @author Franklin Canduri
 */
@Value
@Builder
public class BasketsDisposedEvent {
    UUID eventId;
    Instant occurredOn;
    Long quantity;
    Money lossAmount;

    public static BasketsDisposedEvent of(Long quantity, Money lossAmount) {
        return BasketsDisposedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .quantity(quantity)
            .lossAmount(lossAmount)
            .build();
    }
}
