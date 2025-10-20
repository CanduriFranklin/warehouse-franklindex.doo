package br.com.dio.warehouse.adapter.in.web.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dio.warehouse.adapter.in.web.dto.auth.JwtAuthenticationResponse;
import br.com.dio.warehouse.adapter.in.web.dto.auth.LoginRequest;
import br.com.dio.warehouse.domain.model.Usuario;
import br.com.dio.warehouse.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple Authentication Controller - para teste e isolamento do problema
 * Não usa AuthenticationManager para evitar dependência circular
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/simple-auth")
@RequiredArgsConstructor
public class SimpleAuthenticationController {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> simpleLogin(
            @RequestBody LoginRequest loginRequest
    ) {
        try {
            log.info("Simple login attempt for user: {}", loginRequest.username());
            
            // Buscar usuário no banco
            Usuario usuario = usuarioRepository.findByUsername(loginRequest.username())
                    .orElse(null);
            
            if (usuario == null) {
                log.error("User not found: {}", loginRequest.username());
                return ResponseEntity.status(401).build();
            }
            
            // Verificar senha
            if (!passwordEncoder.matches(loginRequest.password(), usuario.getSenhaHash())) {
                log.error("Invalid password for user: {}", loginRequest.username());
                return ResponseEntity.status(401).build();
            }
            
            // Gerar token simples
            String roles = usuario.getRoles().stream()
                    .map(role -> "ROLE_" + role.getNome().name())
                    .reduce((a, b) -> a + "," + b)
                    .orElse("ROLE_USER");
            
            // Gerar JWT token usando o tokenProvider
            String token = "simple-jwt-" + System.currentTimeMillis(); // Token simples para teste
            Instant expiresAt = Instant.now().plusMillis(86400000); // 24h
            
            JwtAuthenticationResponse response = JwtAuthenticationResponse.of(
                    token, // Token simples para teste
                    expiresAt,
                    usuario.getUsername(),
                    roles
            );
            
            log.info("Simple authentication successful for user: {}", loginRequest.username());
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            log.error("Simple authentication failed for user: {} - {}", 
                    loginRequest.username(), ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}