package br.com.dio.warehouse.domain.exception;

/**
 * Exception thrown when there is insufficient stock
 * 
 * @author Franklin Canduri
 */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(Long requested, Long available) {
        super(String.format("Insufficient stock. Requested: %d, Available: %d", requested, available));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
