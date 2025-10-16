package br.com.dio.warehouse.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JwtAuthenticationFilter
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@DisplayName("JwtAuthenticationFilter Unit Tests")
class JwtAuthenticationFilterTest {
    
    private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
            "this-is-a-test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits".getBytes()
    );
    
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(TEST_SECRET, 3600000L, "test");
    private final JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(tokenProvider);
    
    @Test
    @DisplayName("Should set authentication context when valid token is provided")
    void shouldSetAuthenticationContextWhenValidTokenProvided() throws Exception {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("testuser");
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
    }
    
    @Test
    @DisplayName("Should not set authentication when token is missing")
    void shouldNotSetAuthenticationWhenTokenMissing() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("Should not set authentication when token is invalid")
    void shouldNotSetAuthenticationWhenTokenInvalid() throws Exception {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("Should not set authentication when authorization header is malformed")
    void shouldNotSetAuthenticationWhenAuthorizationHeaderMalformed() throws Exception {
        // Given - Missing "Bearer" prefix
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
    
    @Test
    @DisplayName("Should extract roles and set authorities correctly")
    void shouldExtractRolesAndSetAuthoritiesCorrectly() throws Exception {
        // Given
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN,ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .hasSize(2)
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
    
    @Test
    @DisplayName("Should handle token with single role")
    void shouldHandleTokenWithSingleRole() throws Exception {
        // Given
        String token = tokenProvider.generateToken("user", "ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }
    
    @Test
    @DisplayName("Should continue filter chain after processing")
    void shouldContinueFilterChainAfterProcessing() throws Exception {
        // Given
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then - Filter chain should have been called
        assertThat(filterChain.getRequest()).isEqualTo(request);
    }
    
    @Test
    @DisplayName("Should handle case-sensitive bearer prefix")
    void shouldHandleCaseSensitiveBearerPrefix() throws Exception {
        // Given - Using lowercase "bearer" instead of "Bearer"
        String token = tokenProvider.generateToken("testuser", "ROLE_USER");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();
        
        // Clear context before test
        SecurityContextHolder.clearContext();
        
        // When
        authenticationFilter.doFilterInternal(request, response, filterChain);
        
        // Then - Should not set authentication (case-sensitive)
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
