package br.com.dio.warehouse.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.dio.warehouse.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

/**
 * Security Configuration
 * Configures Spring Security with JWT authentication
 * 
 * Features:
 * - Stateless session management (JWT-based)
 * - CORS configuration for cross-origin requests
 * - CSRF disabled (not needed for stateless APIs)
 * - Endpoint protection with role-based access
 * - Custom JWT authentication filter
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * Configure security filter chain
     * 
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT APIs)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Stateless session management
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        
                        // Public product browsing - GET only
                        .requestMatchers(HttpMethod.GET, "/api/v1/produtos/**").permitAll()
                        
                        // Delivery endpoints - requires ADMIN or WAREHOUSE_MANAGER role
                        .requestMatchers(HttpMethod.POST, "/api/v1/deliveries").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        
                        // Basket sale endpoints - requires ADMIN, WAREHOUSE_MANAGER, or SALES role
                        .requestMatchers(HttpMethod.POST, "/api/v1/baskets/sell").hasAnyRole("ADMIN", "WAREHOUSE_MANAGER", "SALES")
                        
                        // Cart endpoints - requires authentication
                        .requestMatchers("/api/v1/carrinhos/**").authenticated()
                        
                        // Other query endpoints - requires authentication
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()
                        
                        // Actuator endpoints - requires ADMIN role
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Configure CORS (Cross-Origin Resource Sharing)
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins in development (restrict in production)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Allow common headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Password encoder bean
     * Uses BCrypt with strength 12
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    /**
     * Authentication manager bean
     * 
     * @param authConfig Authentication configuration
     * @return AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
