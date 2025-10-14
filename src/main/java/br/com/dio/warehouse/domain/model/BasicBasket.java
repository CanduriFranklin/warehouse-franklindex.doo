package br.com.dio.warehouse.domain.model;

import br.com.dio.warehouse.domain.valueobject.Money;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BasicBasket Entity - Aggregate Root
 * Represents a basic food basket in the warehouse inventory
 * 
 * @author Franklin Canduri
 */
@Entity
@Table(name = "basic_baskets", indexes = {
    @Index(name = "idx_basket_validation_date", columnList = "validation_date"),
    @Index(name = "idx_basket_status", columnList = "status"),
    @Index(name = "idx_basket_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BasicBasket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "validation_date", nullable = false)
    private LocalDate validationDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false, precision = 10, scale = 2))
    })
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BasketStatus status = BasketStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_box_id")
    private DeliveryBox deliveryBox;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "disposed_at")
    private LocalDateTime disposedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    // Business Methods

    /**
     * Check if the basket is expired
     */
    public boolean isExpired() {
        return validationDate.isBefore(LocalDate.now());
    }

    /**
     * Check if the basket is available for sale
     */
    public boolean isAvailable() {
        return status == BasketStatus.AVAILABLE && !isExpired();
    }

    /**
     * Mark basket as sold
     */
    public void markAsSold() {
        if (!isAvailable()) {
            throw new IllegalStateException("Basket is not available for sale");
        }
        this.status = BasketStatus.SOLD;
        this.soldAt = LocalDateTime.now();
    }

    /**
     * Mark basket as disposed (expired)
     */
    public void markAsDisposed() {
        if (status == BasketStatus.SOLD) {
            throw new IllegalStateException("Cannot dispose a sold basket");
        }
        this.status = BasketStatus.DISPOSED;
        this.disposedAt = LocalDateTime.now();
    }

    /**
     * Calculate days until expiration
     */
    public long daysUntilExpiration() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), validationDate);
    }

    public enum BasketStatus {
        AVAILABLE,
        SOLD,
        DISPOSED,
        RESERVED
    }
}
