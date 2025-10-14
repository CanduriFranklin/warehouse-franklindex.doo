package br.com.dio.warehouse.adapter.in.web.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Problem Details for HTTP APIs (RFC 7807)
 * Standard error response format
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response following RFC 7807 Problem Details standard")
public record ProblemDetail(
        
        @Schema(description = "HTTP status code", example = "400")
        Integer status,
        
        @Schema(description = "Error type URI", example = "https://api.warehouse.com/errors/validation-error")
        String type,
        
        @Schema(description = "Short error title", example = "Validation Error")
        String title,
        
        @Schema(description = "Detailed error message", example = "One or more fields have validation errors")
        String detail,
        
        @Schema(description = "URI reference that identifies the specific occurrence of the problem", 
                example = "/api/v1/deliveries")
        String instance,
        
        @Schema(description = "Timestamp when the error occurred", example = "2025-10-14T15:30:00")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        
        @Schema(description = "Field validation errors")
        List<FieldError> errors,
        
        @Schema(description = "Additional error properties")
        Map<String, Object> additionalProperties
) {
    
    /**
     * Creates a ProblemDetail with basic information
     */
    public static ProblemDetail of(Integer status, String title, String detail, String instance) {
        return ProblemDetail.builder()
                .status(status)
                .type(buildTypeUri(status))
                .title(title)
                .detail(detail)
                .instance(instance)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a ProblemDetail with validation errors
     */
    public static ProblemDetail ofValidation(String detail, String instance, List<FieldError> errors) {
        return ProblemDetail.builder()
                .status(400)
                .type("https://api.warehouse.com/errors/validation-error")
                .title("Validation Error")
                .detail(detail)
                .instance(instance)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
    
    /**
     * Builds type URI based on HTTP status
     */
    private static String buildTypeUri(Integer status) {
        return switch (status) {
            case 400 -> "https://api.warehouse.com/errors/bad-request";
            case 404 -> "https://api.warehouse.com/errors/not-found";
            case 409 -> "https://api.warehouse.com/errors/conflict";
            case 422 -> "https://api.warehouse.com/errors/business-rule-violation";
            case 500 -> "https://api.warehouse.com/errors/internal-server-error";
            default -> "https://api.warehouse.com/errors/error";
        };
    }
}
