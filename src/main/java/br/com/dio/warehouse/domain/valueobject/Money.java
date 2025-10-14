package br.com.dio.warehouse.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money Value Object
 * Represents a monetary value with proper rounding and arithmetic operations
 * 
 * @author Franklin Canduri
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Money implements Serializable, Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private BigDecimal amount;

    /**
     * Factory method to create Money from BigDecimal
     */
    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        return new Money(amount.setScale(SCALE, ROUNDING_MODE));
    }

    /**
     * Factory method to create Money from double
     */
    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    /**
     * Factory method to create Money from String
     */
    public static Money of(String amount) {
        if (amount == null || amount.isBlank()) {
            throw new IllegalArgumentException("Amount cannot be null or empty");
        }
        return of(new BigDecimal(amount));
    }

    /**
     * Factory method for zero money
     */
    public static Money zero() {
        return of(BigDecimal.ZERO);
    }

    /**
     * Add two money values
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null money");
        return Money.of(this.amount.add(other.amount));
    }

    /**
     * Subtract money value
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null money");
        return Money.of(this.amount.subtract(other.amount));
    }

    /**
     * Multiply by a factor
     */
    public Money multiply(double factor) {
        return Money.of(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    /**
     * Multiply by another money value (for percentage calculations)
     */
    public Money multiply(Money other) {
        Objects.requireNonNull(other, "Cannot multiply by null money");
        return Money.of(this.amount.multiply(other.amount));
    }

    /**
     * Divide by a divisor
     */
    public Money divide(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return Money.of(this.amount.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING_MODE));
    }

    /**
     * Divide by another money value
     */
    public Money divide(Money other) {
        Objects.requireNonNull(other, "Cannot divide by null money");
        if (other.isZero()) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return Money.of(this.amount.divide(other.amount, SCALE, ROUNDING_MODE));
    }

    /**
     * Check if amount is positive
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Check if amount is zero
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if this money is greater than another
     */
    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Check if this money is less than another
     */
    public boolean isLessThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Get absolute value
     */
    public Money abs() {
        return Money.of(this.amount.abs());
    }

    /**
     * Negate the money value
     */
    public Money negate() {
        return Money.of(this.amount.negate());
    }

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Cannot compare with null money");
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
