package br.com.dio.warehouse.domain.model;

import br.com.dio.warehouse.domain.valueobject.Money;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DeliveryBox Entity - Aggregate Root
 * Represents a delivery of basic baskets received by the warehouse
 * 
 * @author Franklin Canduri
 */
@Entity
@Table(name = "delivery_boxes", indexes = {
    @Index(name = "idx_delivery_received_at", columnList = "received_at"),
    @Index(name = "idx_delivery_validation_date", columnList = "validation_date")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "baskets")
public class DeliveryBox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "total_quantity", nullable = false)
    private Long totalQuantity;

    @Column(name = "validation_date", nullable = false)
    private LocalDate validationDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "total_cost", nullable = false, precision = 10, scale = 2))
    })
    private Money totalCost;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2))
    })
    private Money unitCost;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "selling_price", nullable = false, precision = 10, scale = 2))
    })
    private Money sellingPrice;

    @Column(name = "profit_margin", nullable = false)
    private Double profitMargin;

    @OneToMany(mappedBy = "deliveryBox", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BasicBasket> baskets = new ArrayList<>();

    @CreatedDate
    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Business Methods

    /**
     * Calculate unit cost from total cost and quantity
     */
    public Money calculateUnitCost() {
        return totalCost.divide(totalQuantity);
    }

    /**
     * Calculate selling price with profit margin
     */
    public Money calculateSellingPrice(Double marginPercentage) {
        Money unitPrice = calculateUnitCost();
        return unitPrice.add(unitPrice.multiply(marginPercentage));
    }

    /**
     * Generate baskets for this delivery
     */
    public List<BasicBasket> generateBaskets(Money calculatedSellingPrice) {
        List<BasicBasket> generatedBaskets = new ArrayList<>();
        
        for (long i = 0; i < totalQuantity; i++) {
            BasicBasket basket = BasicBasket.builder()
                .validationDate(this.validationDate)
                .price(calculatedSellingPrice)
                .status(BasicBasket.BasketStatus.AVAILABLE)
                .deliveryBox(this)
                .build();
            generatedBaskets.add(basket);
        }
        
        this.baskets.addAll(generatedBaskets);
        return generatedBaskets;
    }

    /**
     * Get count of available baskets
     */
    public long getAvailableCount() {
        return baskets.stream()
            .filter(BasicBasket::isAvailable)
            .count();
    }

    /**
     * Get count of expired baskets
     */
    public long getExpiredCount() {
        return baskets.stream()
            .filter(BasicBasket::isExpired)
            .count();
    }
}
