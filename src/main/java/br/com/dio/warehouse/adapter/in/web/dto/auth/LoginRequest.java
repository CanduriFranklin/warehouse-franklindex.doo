package br.com.dio.warehouse.adapter.in.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login Request DTO
 * 
 * @param username User identifier
 * @param password User password
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
public record LoginRequest(
        
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password
) {
}
