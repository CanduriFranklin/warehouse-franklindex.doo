package br.com.dio.warehouse.infrastructure.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * In-Memory User Details Service
 * Provides user authentication details for testing and development
 * 
 * SECURITY WARNING: This implementation is for DEVELOPMENT/TESTING ONLY
 * - Credentials are loaded from environment variables
 * - NEVER use hardcoded credentials in production
 * - DEPRECATED: Replaced with DatabaseUserDetailsService for database-backed authentication
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 * 
 * @deprecated Use DatabaseUserDetailsService instead. This service is kept for reference only.
 */
@Slf4j
// @Service - DISABLED: Using DatabaseUserDetailsService instead
@Deprecated(since = "2.0.0", forRemoval = true)
public class InMemoryUserDetailsService implements UserDetailsService {
    
    private final Map<String, UserDetails> users;
    
    public InMemoryUserDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${dev.users.admin.username:admin}") String adminUsername,
            @Value("${dev.users.admin.password:#{null}}") String adminPassword,
            @Value("${dev.users.manager.username:manager}") String managerUsername,
            @Value("${dev.users.manager.password:#{null}}") String managerPassword,
            @Value("${dev.users.sales.username:sales}") String salesUsername,
            @Value("${dev.users.sales.password:#{null}}") String salesPassword) {
        
        // Validate that passwords are provided via environment variables
        if (adminPassword == null || managerPassword == null || salesPassword == null) {
            log.error("SECURITY ERROR: User passwords must be provided via environment variables!");
            log.error("Set dev.users.admin.password, dev.users.manager.password, dev.users.sales.password");
            throw new IllegalStateException(
                "User passwords not configured. Check environment variables or application.yml"
            );
        }
        
        // Create in-memory users for testing (credentials from environment variables)
        this.users = Map.of(
                adminUsername, User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_WAREHOUSE_MANAGER")
                        )
                        .build(),
                
                managerUsername, User.builder()
                        .username(managerUsername)
                        .password(passwordEncoder.encode(managerPassword))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_WAREHOUSE_MANAGER"),
                                new SimpleGrantedAuthority("ROLE_SALES")
                        )
                        .build(),
                
                salesUsername, User.builder()
                        .username(salesUsername)
                        .password(passwordEncoder.encode(salesPassword))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_SALES")
                        )
                        .build()
        );
        
        log.info("InMemoryUserDetailsService initialized with {} users (credentials from environment)", 
                users.size());
        log.warn("DEVELOPMENT MODE: Using in-memory authentication. Replace with database in production!");
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        
        if (user == null) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        log.debug("User found: {}", username);
        return user;
    }
}
