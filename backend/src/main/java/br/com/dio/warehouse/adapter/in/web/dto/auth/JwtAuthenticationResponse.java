package br.com.dio.warehouse.adapter.in.web.dto.auth;

import java.time.Instant;

/**
 * JWT Authentication Response DTO
 * 
 * @param token JWT access token
 * @param type Token type (always "Bearer")
 * @param expiresAt Token expiration timestamp
 * @param username Authenticated user identifier
 * @param roles User roles (comma-separated)
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
public record JwtAuthenticationResponse(
        String token,
        String type,
        Instant expiresAt,
        String username,
        String roles
) {
    
    /**
     * Factory method to create JWT response
     * 
     * @param token JWT token
     * @param expiresAt Expiration timestamp
     * @param username User identifier
     * @param roles User roles
     * @return JwtAuthenticationResponse
     */
    public static JwtAuthenticationResponse of(
            String token, 
            Instant expiresAt, 
            String username, 
            String roles
    ) {
        return new JwtAuthenticationResponse(token, "Bearer", expiresAt, username, roles);
    }
}
