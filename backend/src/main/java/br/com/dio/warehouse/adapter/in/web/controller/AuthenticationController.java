package br.com.dio.warehouse.adapter.in.web.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dio.warehouse.adapter.in.web.dto.auth.JwtAuthenticationResponse;
import br.com.dio.warehouse.adapter.in.web.dto.auth.LoginRequest;
import br.com.dio.warehouse.adapter.in.web.dto.auth.RegisterRequest;
import br.com.dio.warehouse.application.service.UsuarioRegistrationService;
import br.com.dio.warehouse.domain.model.Usuario;
import br.com.dio.warehouse.infrastructure.security.DatabaseUserDetailsService;
import br.com.dio.warehouse.infrastructure.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication Controller
 * Handles user authentication and JWT token generation
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and JWT token management")
public class AuthenticationController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRegistrationService registrationService;
    private final DatabaseUserDetailsService userDetailsService;
    
    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest Login credentials
     * @return JWT authentication response
     */
    @Operation(
            summary = "User Login",
            description = "Authenticate user with username and password, returns JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String token = tokenProvider.generateToken(authentication);
            
            // Extract roles
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            
            // Calculate expiration
            Instant expiresAt = Instant.now()
                    .plusMillis(tokenProvider.getExpirationMs());
            
            // Build response
            JwtAuthenticationResponse response = JwtAuthenticationResponse.of(
                    token,
                    expiresAt,
                    authentication.getName(),
                    roles
            );
            
            log.info("User authenticated successfully: {}", loginRequest.username());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for user: {}", loginRequest.username());
            throw ex;
        }
    }
    
    /**
     * Register new user
     * 
     * @param registerRequest Registration data
     * @return JWT authentication response with auto-login
     */
    @Operation(
            summary = "User Registration",
            description = "Register a new user account and perform automatic login"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error (invalid input, username/email duplicate, etc)"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        try {
            log.info("New user registration request: {}", registerRequest.username());
            
            // Register new user
            Usuario novoUsuario = registrationService.registrarNovoUsuario(registerRequest);
            
            // Perform automatic login
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            novoUsuario.getUsername(),
                            registerRequest.password()
                    )
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Update last access
            userDetailsService.updateUltimoAcesso(novoUsuario.getUsername());
            
            // Generate JWT token
            String token = tokenProvider.generateToken(authentication);
            
            // Extract roles
            String roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            
            // Calculate expiration
            Instant expiresAt = Instant.now()
                    .plusMillis(tokenProvider.getExpirationMs());
            
            // Build response
            JwtAuthenticationResponse response = JwtAuthenticationResponse.of(
                    token,
                    expiresAt,
                    novoUsuario.getUsername(),
                    roles
            );
            
            log.info("User registered and authenticated successfully: {}", registerRequest.username());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException ex) {
            log.warn("Registration validation failed: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            log.error("Registration failed: {}", ex.getMessage());
            return ResponseEntity.status(400).build();
        }
    }
    
    /**
     * Logout user (client-side token removal)
     * 
     * @return Success message
     */
    @Operation(
            summary = "User Logout",
            description = "Logout user (JWT is stateless, client should discard the token)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
        return ResponseEntity.ok("Logout successful");
    }
    
    /**
     * Get current authenticated user info
     * 
     * @return Current user information
     */
    @Operation(
            summary = "Get Current User",
            description = "Get information about the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User information retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<JwtAuthenticationResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        
        // Extract roles
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        // Build response with user info
        JwtAuthenticationResponse response = JwtAuthenticationResponse.of(
                null, // No token in response
                Instant.now().plusMillis(tokenProvider.getExpirationMs()),
                authentication.getName(),
                roles
        );
        
        log.info("Current user info retrieved: {}", authentication.getName());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Validate JWT token
     * 
     * @return Token validation status
     */
    @Operation(
            summary = "Validate Token",
            description = "Check if the current JWT token is valid"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token is invalid or expired"
            )
    })
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Token is valid");
        }
        
        return ResponseEntity.status(401).body("Token is invalid");
    }
}
