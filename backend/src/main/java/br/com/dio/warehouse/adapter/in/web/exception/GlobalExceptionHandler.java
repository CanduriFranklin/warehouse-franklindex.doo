package br.com.dio.warehouse.adapter.in.web.exception;

import br.com.dio.warehouse.adapter.in.web.dto.error.FieldError;
import br.com.dio.warehouse.adapter.in.web.dto.error.ProblemDetail;
import br.com.dio.warehouse.domain.exception.BasketNotFoundException;
import br.com.dio.warehouse.domain.exception.BusinessRuleViolationException;
import br.com.dio.warehouse.domain.exception.DomainException;
import br.com.dio.warehouse.domain.exception.InsufficientStockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers
 * Implements RFC 7807 Problem Details for HTTP APIs
 * 
 * @author Franklin Canduri
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    // ========== Domain Exceptions ==========
    
    /**
     * Handles BasketNotFoundException (404 Not Found)
     */
    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleBasketNotFound(
            BasketNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Basket not found: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.NOT_FOUND.value(),
                "Basket Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
    
    /**
     * Handles InsufficientStockException (422 Unprocessable Entity)
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientStock(
            InsufficientStockException ex,
            HttpServletRequest request) {
        
        log.warn("Insufficient stock: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Insufficient Stock",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }
    
    /**
     * Handles BusinessRuleViolationException (422 Unprocessable Entity)
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRuleViolation(
            BusinessRuleViolationException ex,
            HttpServletRequest request) {
        
        log.warn("Business rule violation: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Business Rule Violation",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }
    
    /**
     * Handles generic DomainException (422 Unprocessable Entity)
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {
        
        log.warn("Domain exception: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Domain Error",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }
    
    // ========== Validation Exceptions ==========
    
    /**
     * Handles MethodArgumentNotValidException (Bean Validation on @RequestBody)
     * Returns 400 Bad Request with field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        BindingResult bindingResult = ex.getBindingResult();
        
        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> FieldError.of(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());
        
        log.warn("Validation failed for request {}: {} errors", 
                request.getRequestURI(), fieldErrors.size());
        
        ProblemDetail problem = ProblemDetail.ofValidation(
                String.format("Validation failed for %d field(s)", fieldErrors.size()),
                request.getRequestURI(),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(problem);
    }
    
    /**
     * Handles ConstraintViolationException (Bean Validation on @PathVariable, @RequestParam)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        List<FieldError> fieldErrors = ex.getConstraintViolations().stream()
                .map(violation -> FieldError.of(
                        extractFieldName(violation),
                        violation.getInvalidValue(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());
        
        log.warn("Constraint violation for request {}: {} errors", 
                request.getRequestURI(), fieldErrors.size());
        
        ProblemDetail problem = ProblemDetail.ofValidation(
                "Constraint violation in request parameters",
                request.getRequestURI(),
                fieldErrors
        );
        
        return ResponseEntity.badRequest().body(problem);
    }
    
    /**
     * Handles HttpMessageNotReadableException (malformed JSON)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        log.warn("Malformed JSON request: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed Request",
                "Failed to read request body. Please check your JSON syntax.",
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(problem);
    }
    
    /**
     * Handles MethodArgumentTypeMismatchException (type conversion errors)
     * Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        
        log.warn("Type mismatch for parameter '{}': expected {}", paramName, requiredType);
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch",
                String.format("Parameter '%s' must be of type %s", paramName, requiredType),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(problem);
    }
    
    // ========== Generic Exceptions ==========
    
    /**
     * Handles IllegalArgumentException (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(problem);
    }
    
    /**
     * Handles all uncaught exceptions (500 Internal Server Error)
     * This is a fallback handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error occurred: ", ex);
        
        ProblemDetail problem = ProblemDetail.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later or contact support.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Extracts field name from ConstraintViolation path
     */
    private String extractFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] parts = propertyPath.split("\\.");
        return parts[parts.length - 1];
    }
}
