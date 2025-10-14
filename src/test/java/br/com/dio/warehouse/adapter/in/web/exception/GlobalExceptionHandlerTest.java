package br.com.dio.warehouse.adapter.in.web.exception;

import br.com.dio.warehouse.adapter.in.web.dto.error.ProblemDetail;
import br.com.dio.warehouse.domain.exception.BasketNotFoundException;
import br.com.dio.warehouse.domain.exception.BusinessRuleViolationException;
import br.com.dio.warehouse.domain.exception.DomainException;
import br.com.dio.warehouse.domain.exception.InsufficientStockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler
 * Tests all exception handling scenarios
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {
    
    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    private static final String REQUEST_URI = "/api/v1/test";
    
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(REQUEST_URI);
    }
    
    // ========== Domain Exception Tests ==========
    
    @Test
    @DisplayName("Should handle BasketNotFoundException with 404 status")
    void shouldHandleBasketNotFoundException() {
        // Given
        UUID basketId = UUID.randomUUID();
        BasketNotFoundException exception = new BasketNotFoundException(basketId);
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleBasketNotFound(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().title()).isEqualTo("Basket Not Found");
        assertThat(response.getBody().detail()).contains(basketId.toString());
        assertThat(response.getBody().instance()).isEqualTo(REQUEST_URI);
        assertThat(response.getBody().timestamp()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle InsufficientStockException with 422 status")
    void shouldHandleInsufficientStockException() {
        // Given
        InsufficientStockException exception = new InsufficientStockException(
                "Insufficient stock. Available: 5, Requested: 10"
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleInsufficientStock(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().title()).isEqualTo("Insufficient Stock");
        assertThat(response.getBody().detail()).contains("Insufficient stock");
        assertThat(response.getBody().instance()).isEqualTo(REQUEST_URI);
    }
    
    @Test
    @DisplayName("Should handle BusinessRuleViolationException with 422 status")
    void shouldHandleBusinessRuleViolationException() {
        // Given
        BusinessRuleViolationException exception = new BusinessRuleViolationException(
                "Cannot sell expired baskets"
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleBusinessRuleViolation(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().title()).isEqualTo("Business Rule Violation");
        assertThat(response.getBody().detail()).contains("Cannot sell expired baskets");
    }
    
    @Test
    @DisplayName("Should handle generic DomainException with 422 status")
    void shouldHandleGenericDomainException() {
        // Given
        DomainException exception = new DomainException("Generic domain error");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleDomainException(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(422);
        assertThat(response.getBody().title()).isEqualTo("Domain Error");
        assertThat(response.getBody().detail()).contains("Generic domain error");
    }
    
    // ========== Validation Exception Tests ==========
    
    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "request");
        bindingResult.addError(new FieldError("request", "totalQuantity", -5, false, 
                null, null, "Total quantity must be positive"));
        bindingResult.addError(new FieldError("request", "totalCost", null, false, 
                null, null, "Total cost is required"));
        
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleMethodArgumentNotValid(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Validation Error");
        assertThat(response.getBody().detail()).contains("Validation failed for 2 field(s)");
        assertThat(response.getBody().errors()).hasSize(2);
        assertThat(response.getBody().errors().get(0).field()).isEqualTo("totalQuantity");
        assertThat(response.getBody().errors().get(0).rejectedValue()).isEqualTo(-5);
        assertThat(response.getBody().errors().get(1).field()).isEqualTo("totalCost");
    }
    
    @Test
    @DisplayName("Should handle ConstraintViolationException with parameter violations")
    void shouldHandleConstraintViolationException() {
        // Given - Create a real ConstraintViolation using TestConstraintViolation
        TestConstraintViolation violation = new TestConstraintViolation(
                "sellBaskets.quantity",
                -10,
                "must be positive"
        );
        
        ConstraintViolationException exception = new ConstraintViolationException(
                "Constraint violation", 
                Set.of(violation)
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleConstraintViolation(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Validation Error");
        assertThat(response.getBody().errors()).hasSize(1);
        assertThat(response.getBody().errors().get(0).field()).isEqualTo("quantity");
        assertThat(response.getBody().errors().get(0).rejectedValue()).isEqualTo(-10);
    }
    
    /**
     * Test implementation of ConstraintViolation for unit testing
     */
    private static class TestConstraintViolation implements ConstraintViolation<Object> {
        private final String propertyPath;
        private final Object invalidValue;
        private final String message;
        
        public TestConstraintViolation(String propertyPath, Object invalidValue, String message) {
            this.propertyPath = propertyPath;
            this.invalidValue = invalidValue;
            this.message = message;
        }
        
        @Override
        public String getMessage() {
            return message;
        }
        
        @Override
        public String getMessageTemplate() {
            return message;
        }
        
        @Override
        public Object getRootBean() {
            return null;
        }
        
        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }
        
        @Override
        public Object getLeafBean() {
            return null;
        }
        
        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }
        
        @Override
        public Object getExecutableReturnValue() {
            return null;
        }
        
        @Override
        public Path getPropertyPath() {
            return new Path() {
                @Override
                public java.util.Iterator<Node> iterator() {
                    return java.util.Collections.emptyIterator();
                }
                
                @Override
                public String toString() {
                    return propertyPath;
                }
            };
        }
        
        @Override
        public Object getInvalidValue() {
            return invalidValue;
        }
        
        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }
        
        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }
    
    @Test
    @DisplayName("Should handle HttpMessageNotReadableException for malformed JSON")
    void shouldHandleHttpMessageNotReadableException() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                "JSON parse error", 
                (org.springframework.http.HttpInputMessage) null
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleHttpMessageNotReadable(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Malformed Request");
        assertThat(response.getBody().detail()).contains("JSON syntax");
    }
    
    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException for type conversion errors")
    void shouldHandleMethodArgumentTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc", 
                Long.class, 
                "quantity", 
                null, 
                null
        );
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleMethodArgumentTypeMismatch(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Type Mismatch");
        assertThat(response.getBody().detail()).contains("quantity");
        assertThat(response.getBody().detail()).contains("Long");
    }
    
    // ========== Generic Exception Tests ==========
    
    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 status")
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid quantity value");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleIllegalArgument(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().title()).isEqualTo("Invalid Argument");
        assertThat(response.getBody().detail()).contains("Invalid quantity value");
    }
    
    @Test
    @DisplayName("Should handle unexpected Exception with 500 status")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected database error");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleGenericException(exception, request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().title()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().detail()).contains("unexpected error");
        assertThat(response.getBody().detail()).doesNotContain("database"); // Should not leak internal details
    }
    
    // ========== RFC 7807 Compliance Tests ==========
    
    @Test
    @DisplayName("Should include all RFC 7807 required fields in error response")
    void shouldIncludeAllRfc7807Fields() {
        // Given
        DomainException exception = new DomainException("Test error");
        
        // When
        ResponseEntity<ProblemDetail> response = exceptionHandler.handleDomainException(exception, request);
        
        // Then
        ProblemDetail problem = response.getBody();
        assertThat(problem).isNotNull();
        assertThat(problem.status()).isNotNull();
        assertThat(problem.type()).isNotNull().startsWith("https://");
        assertThat(problem.title()).isNotNull().isNotBlank();
        assertThat(problem.detail()).isNotNull().isNotBlank();
        assertThat(problem.instance()).isNotNull().isEqualTo(REQUEST_URI);
        assertThat(problem.timestamp()).isNotNull();
    }
}
