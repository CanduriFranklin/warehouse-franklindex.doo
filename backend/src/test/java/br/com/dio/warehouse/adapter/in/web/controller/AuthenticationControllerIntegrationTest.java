package br.com.dio.warehouse.adapter.in.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.dio.warehouse.adapter.in.web.dto.auth.JwtAuthenticationResponse;
import br.com.dio.warehouse.adapter.in.web.dto.auth.LoginRequest;
import br.com.dio.warehouse.infrastructure.security.JwtTokenProvider;

/**
 * Integration tests for AuthenticationController
 * Tests authentication endpoints with MockMvc
 * 
 * NOTE: Temporarily disabled due to Java 25 compatibility issues with Spring Boot component scanning
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Authentication Controller Integration Tests")
@Disabled("Temporarily disabled due to Java 25 compatibility issues with Spring Boot")
class AuthenticationControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    // ========== Login Tests ==========
    
    @Test
    @DisplayName("Should authenticate admin user successfully")
    void shouldAuthenticateAdminUserSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        
        // When & Then
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").value(containsString("ROLE_ADMIN")))
                .andExpect(jsonPath("$.expiresAt").exists())
                .andReturn();
        
        // Verify token is valid
        String responseBody = result.getResponse().getContentAsString();
        JwtAuthenticationResponse response = objectMapper.readValue(responseBody, JwtAuthenticationResponse.class);
        
        assertThat(response.token()).isNotNull();
        assertThat(tokenProvider.validateToken(response.token())).isTrue();
        assertThat(tokenProvider.getUsernameFromToken(response.token())).isEqualTo("admin");
    }
    
    @Test
    @DisplayName("Should authenticate manager user successfully")
    void shouldAuthenticateManagerUserSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("manager", "manager123");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("manager"))
                .andExpect(jsonPath("$.roles").value(containsString("ROLE_WAREHOUSE_MANAGER")))
                .andExpect(jsonPath("$.roles").value(containsString("ROLE_SALES")));
    }
    
    @Test
    @DisplayName("Should authenticate sales user successfully")
    void shouldAuthenticateSalesUserSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("sales", "sales123");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("sales"))
                .andExpect(jsonPath("$.roles").value("ROLE_SALES"));
    }
    
    @Test
    @DisplayName("Should reject login with invalid credentials")
    void shouldRejectLoginWithInvalidCredentials() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin", "wrongpassword");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should reject login with non-existent user")
    void shouldRejectLoginWithNonExistentUser() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("nonexistent", "password123");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should reject login with empty username")
    void shouldRejectLoginWithEmptyUsername() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("", "password123");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("username")));
    }
    
    @Test
    @DisplayName("Should reject login with empty password")
    void shouldRejectLoginWithEmptyPassword() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin", "");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("password")));
    }
    
    @Test
    @DisplayName("Should reject login with short username")
    void shouldRejectLoginWithShortUsername() throws Exception {
        // Given - username too short (less than 3 characters)
        LoginRequest loginRequest = new LoginRequest("ab", "password123");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("username")));
    }
    
    @Test
    @DisplayName("Should reject login with short password")
    void shouldRejectLoginWithShortPassword() throws Exception {
        // Given - password too short (less than 6 characters)
        LoginRequest loginRequest = new LoginRequest("admin", "12345");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].field").value(hasItem("password")));
    }
    
    // ========== Token Validation Tests ==========
    
    @Test
    @DisplayName("Should validate valid token")
    @WithMockUser(username = "testuser")
    void shouldValidateValidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }
    
    @Test
    @DisplayName("Should reject validation without token")
    void shouldRejectValidationWithoutToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should accept request with valid JWT token")
    void shouldAcceptRequestWithValidJwtToken() throws Exception {
        // Given - Generate a valid token
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN");
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }
    
    @Test
    @DisplayName("Should reject request with invalid JWT token")
    void shouldRejectRequestWithInvalidJwtToken() throws Exception {
        // Given - Invalid token
        String invalidToken = "invalid.jwt.token";
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", "Bearer " + invalidToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should reject request with malformed authorization header")
    void shouldRejectRequestWithMalformedAuthorizationHeader() throws Exception {
        // Given - Malformed header (missing "Bearer" prefix)
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN");
        
        // When & Then
        mockMvc.perform(get("/api/v1/auth/validate")
                        .header("Authorization", token))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    // ========== Logout Tests ==========
    
    @Test
    @DisplayName("Should logout successfully")
    void shouldLogoutSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }
    
    // ========== Protected Endpoint Tests ==========
    
    @Test
    @DisplayName("Should access protected endpoint with valid token")
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        // Given - Generate a valid admin token
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN,ROLE_WAREHOUSE_MANAGER");
        
        // When & Then - Try to access a protected endpoint (health actuator)
        mockMvc.perform(get("/actuator/health")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Should deny access to protected endpoint without token")
    void shouldDenyAccessToProtectedEndpointWithoutToken() throws Exception {
        // When & Then - Try to access a protected endpoint without token
        mockMvc.perform(get("/actuator/metrics"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("Should allow access to public endpoints")
    void shouldAllowAccessToPublicEndpoints() throws Exception {
        // When & Then - Public endpoints should be accessible without authentication
        mockMvc.perform(get("/actuator/health"))
                .andDo(print())
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/actuator/info"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    // ========== Role-Based Access Tests ==========
    
    @Test
    @DisplayName("Should allow admin to access admin endpoints")
    void shouldAllowAdminToAccessAdminEndpoints() throws Exception {
        // Given - Generate a valid admin token
        String token = tokenProvider.generateToken("admin", "ROLE_ADMIN");
        
        // When & Then - Admin should access admin-only actuator endpoints
        mockMvc.perform(get("/actuator/metrics")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("Should deny non-admin access to admin endpoints")
    void shouldDenyNonAdminAccessToAdminEndpoints() throws Exception {
        // Given - Generate a token with non-admin role
        String token = tokenProvider.generateToken("sales", "ROLE_SALES");
        
        // When & Then - Non-admin should be denied access
        mockMvc.perform(get("/actuator/metrics")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    // ========== CORS Tests ==========
    
    @Test
    @DisplayName("Should handle CORS preflight request")
    void shouldHandleCorsPreflight() throws Exception {
        // When & Then
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .options("/api/v1/auth/login")
                                .header("Origin", "http://localhost:3000")
                                .header("Access-Control-Request-Method", "POST")
                                .header("Access-Control-Request-Headers", "Content-Type")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }
}
