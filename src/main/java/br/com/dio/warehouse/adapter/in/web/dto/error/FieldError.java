package br.com.dio.warehouse.adapter.in.web.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a field-level validation error
 * Used in ProblemDetail to provide detailed validation feedback
 */
@Schema(description = "Field validation error details")
public record FieldError(
        
        @Schema(description = "Name of the field that failed validation", example = "totalQuantity")
        String field,
        
        @Schema(description = "Value that was rejected", example = "-5")
        Object rejectedValue,
        
        @Schema(description = "Error message describing why the validation failed", 
                example = "Total quantity must be positive")
        String message
) {
    
    /**
     * Creates a FieldError from field name and message
     */
    public static FieldError of(String field, String message) {
        return new FieldError(field, null, message);
    }
    
    /**
     * Creates a FieldError with all information
     */
    public static FieldError of(String field, Object rejectedValue, String message) {
        return new FieldError(field, rejectedValue, message);
    }
}
