package br.com.dio.warehouse.domain.event;

import br.com.dio.warehouse.domain.valueobject.Money;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public BasketsDisposedEvent(
            @JsonProperty("eventId") UUID eventId,
            @JsonProperty("occurredOn") Instant occurredOn,
            @JsonProperty("quantity") Long quantity,
            @JsonProperty("lossAmount") Money lossAmount
    ) {
        this.eventId = eventId;
        this.occurredOn = occurredOn;
        this.quantity = quantity;
        this.lossAmount = lossAmount;
    }

    public static BasketsDisposedEvent of(Long quantity, Money lossAmount) {
        return BasketsDisposedEvent.builder()
            .eventId(UUID.randomUUID())
            .occurredOn(Instant.now())
            .quantity(quantity)
            .lossAmount(lossAmount)
            .build();
    }
}
