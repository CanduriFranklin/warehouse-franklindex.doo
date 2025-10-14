package br.com.dio.warehouse.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Unit tests for JwtTokenProvider
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {
    
    private JwtTokenProvider tokenProvider;
    
    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
            "this-is-a-test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits".getBytes()
    );
    private static final long TEST_EXPIRATION = 3600000L; // 1 hour
    private static final String TEST_ISSUER = "warehouse-test";
    
    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(TEST_SECRET, TEST_EXPIRATION, TEST_ISSUER);
    }
    
    // ========== Token Generation Tests ==========
    
    @Test
    @DisplayName("Should generate valid JWT token from authentication")
    void shouldGenerateValidTokenFromAuthentication() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )
        );
        
        // When
        String token = tokenProvider.generateToken(authentication);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }
    
    @Test
    @DisplayName("Should generate valid JWT token from username and roles")
    void shouldGenerateValidTokenFromUsernameAndRoles() {
        // Given
        String username = "testuser";
        String roles = "ROLE_ADMIN,ROLE_USER";
        
        // When
        String token = tokenProvider.generateToken(username, roles);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo(username);
        assertThat(tokenProvider.getRolesFromToken(token)).isEqualTo(roles);
    }
    
    // ========== Token Extraction Tests ==========
    
    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Given
        String expectedUsername = "john.doe";
        String token = tokenProvider.generateToken(expectedUsername, "ROLE_USER");
        
        // When
        String actualUsername = tokenProvider.getUsernameFromToken(token);
        
        // Then
        assertThat(actualUsername).isEqualTo(expectedUsername);
    }
    
    @Test
    @DisplayName("Should extract roles from token")
    void shouldExtractRolesFromToken() {
        // Given
        String expectedRoles = "ROLE_ADMIN,ROLE_MANAGER";
        String token = tokenProvider.generateToken("admin", expectedRoles);
        
        // When
        String actualRoles = tokenProvider.getRolesFromToken(token);
        
        // Then
        assertThat(actualRoles).isEqualTo(expectedRoles);
    }
    
    @Test
    @DisplayName("Should extract expiration from token")
    void shouldExtractExpirationFromToken() {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        
        // When
        Date expiration = tokenProvider.getExpirationFromToken(token);
        
        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }
    
    // ========== Token Validation Tests ==========
    
    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        
        // When
        boolean isValid = tokenProvider.validateToken(token);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should reject malformed token")
    void shouldRejectMalformedToken() {
        // Given
        String malformedToken = "this.is.not.a.valid.jwt";
        
        // When
        boolean isValid = tokenProvider.validateToken(malformedToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should reject token with invalid signature")
    void shouldRejectTokenWithInvalidSignature() {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";
        
        // When
        boolean isValid = tokenProvider.validateToken(tamperedToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should reject empty token")
    void shouldRejectEmptyToken() {
        // Given
        String emptyToken = "";
        
        // When
        boolean isValid = tokenProvider.validateToken(emptyToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should reject null token")
    void shouldRejectNullToken() {
        // Given
        String nullToken = null;
        
        // When
        boolean isValid = tokenProvider.validateToken(nullToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    // ========== Token Expiration Tests ==========
    
    @Test
    @DisplayName("Should detect non-expired token")
    void shouldDetectNonExpiredToken() {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        
        // When
        boolean isExpired = tokenProvider.isTokenExpired(token);
        
        // Then
        assertThat(isExpired).isFalse();
    }
    
    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        // Given - Create token provider with very short expiration (1ms)
        JwtTokenProvider shortExpirationProvider = new JwtTokenProvider(
                TEST_SECRET,
                1L, // 1 millisecond
                TEST_ISSUER
        );
        String token = shortExpirationProvider.generateToken("testuser", "ROLE_USER");
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // When
        boolean isExpired = shortExpirationProvider.isTokenExpired(token);
        
        // Then
        assertThat(isExpired).isTrue();
    }
    
    // ========== Token Properties Tests ==========
    
    @Test
    @DisplayName("Should return correct expiration time")
    void shouldReturnCorrectExpirationTime() {
        // When
        long expirationMs = tokenProvider.getExpirationMs();
        
        // Then
        assertThat(expirationMs).isEqualTo(TEST_EXPIRATION);
    }
    
    @Test
    @DisplayName("Should generate tokens with multiple roles")
    void shouldGenerateTokensWithMultipleRoles() {
        // Given
        String username = "multiRoleUser";
        String roles = "ROLE_ADMIN,ROLE_MANAGER,ROLE_USER,ROLE_SALES";
        
        // When
        String token = tokenProvider.generateToken(username, roles);
        
        // Then
        assertThat(tokenProvider.getUsernameFromToken(token)).isEqualTo(username);
        assertThat(tokenProvider.getRolesFromToken(token)).isEqualTo(roles);
    }
    
    @Test
    @DisplayName("Should handle username with special characters")
    void shouldHandleUsernameWithSpecialCharacters() {
        // Given
        String username = "user.name+test@example.com";
        String token = tokenProvider.generateToken(username, "ROLE_USER");
        
        // When
        String extractedUsername = tokenProvider.getUsernameFromToken(token);
        
        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }
}
