package br.com.dio.warehouse.domain.exception;

/**
 * Base Domain Exception
 * 
 * @author Franklin Canduri
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
