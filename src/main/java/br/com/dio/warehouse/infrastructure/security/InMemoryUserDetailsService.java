package br.com.dio.warehouse.infrastructure.security;

import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * In-Memory User Details Service
 * Provides user authentication details for testing and development
 * 
 * TODO: Replace with database-backed implementation in production
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@Service
public class InMemoryUserDetailsService implements UserDetailsService {
    
    private final Map<String, UserDetails> users;
    
    public InMemoryUserDetailsService(PasswordEncoder passwordEncoder) {
        // Create in-memory users for testing
        this.users = Map.of(
                "admin", User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_WAREHOUSE_MANAGER")
                        )
                        .build(),
                
                "manager", User.builder()
                        .username("manager")
                        .password(passwordEncoder.encode("manager123"))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_WAREHOUSE_MANAGER"),
                                new SimpleGrantedAuthority("ROLE_SALES")
                        )
                        .build(),
                
                "sales", User.builder()
                        .username("sales")
                        .password(passwordEncoder.encode("sales123"))
                        .authorities(
                                new SimpleGrantedAuthority("ROLE_SALES")
                        )
                        .build()
        );
        
        log.info("InMemoryUserDetailsService initialized with {} users", users.size());
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
