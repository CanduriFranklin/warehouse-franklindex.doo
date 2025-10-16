package br.com.dio.warehouse.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a basket is not found
 * 
 * @author Franklin Canduri
 */
public class BasketNotFoundException extends DomainException {

    public BasketNotFoundException(UUID id) {
        super("Basket not found with id: " + id);
    }

    public BasketNotFoundException(String message) {
        super(message);
    }
}
