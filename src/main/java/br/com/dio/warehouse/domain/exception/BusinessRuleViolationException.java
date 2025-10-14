package br.com.dio.warehouse.domain.exception;

/**
 * Exception thrown when a business rule is violated
 * 
 * @author Franklin Canduri
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
