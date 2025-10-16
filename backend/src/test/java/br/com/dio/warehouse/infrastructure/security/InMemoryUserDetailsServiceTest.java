package br.com.dio.warehouse.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for InMemoryUserDetailsService
 * Tests using test credentials (not production values)
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@DisplayName("InMemoryUserDetailsService Unit Tests")
class InMemoryUserDetailsServiceTest {
    
    private InMemoryUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    
    // Test credentials (different from production environment variables)
    private static final String TEST_ADMIN_USERNAME = "test_admin";
    private static final String TEST_ADMIN_PASSWORD = "TestAdminPass123!";
    private static final String TEST_MANAGER_USERNAME = "test_manager";
    private static final String TEST_MANAGER_PASSWORD = "TestManagerPass123!";
    private static final String TEST_SALES_USERNAME = "test_sales";
    private static final String TEST_SALES_PASSWORD = "TestSalesPass123!";
    
    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(12);
        userDetailsService = new InMemoryUserDetailsService(
                passwordEncoder,
                TEST_ADMIN_USERNAME,
                TEST_ADMIN_PASSWORD,
                TEST_MANAGER_USERNAME,
                TEST_MANAGER_PASSWORD,
                TEST_SALES_USERNAME,
                TEST_SALES_PASSWORD
        );
    }
    
    // ========== Admin User Tests ==========
    
    @Test
    @DisplayName("Should load admin user successfully")
    void shouldLoadAdminUserSuccessfully() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_ADMIN_USERNAME);
        
        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(TEST_ADMIN_USERNAME);
        assertThat(user.getPassword()).isNotEmpty();
        assertThat(passwordEncoder.matches(TEST_ADMIN_PASSWORD, user.getPassword())).isTrue();
        assertThat(user.getAuthorities())
                .hasSize(2)
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER");
    }
    
    @Test
    @DisplayName("Should verify admin has ADMIN role")
    void shouldVerifyAdminHasAdminRole() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_ADMIN_USERNAME);
        
        // Then
        assertThat(user.getAuthorities())
                .extracting("authority")
                .contains("ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should verify admin has WAREHOUSE_MANAGER role")
    void shouldVerifyAdminHasWarehouseManagerRole() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_ADMIN_USERNAME);
        
        // Then
        assertThat(user.getAuthorities())
                .extracting("authority")
                .contains("ROLE_WAREHOUSE_MANAGER");
    }
    
    // ========== Manager User Tests ==========
    
    @Test
    @DisplayName("Should load manager user successfully")
    void shouldLoadManagerUserSuccessfully() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_MANAGER_USERNAME);
        
        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(TEST_MANAGER_USERNAME);
        assertThat(user.getPassword()).isNotEmpty();
        assertThat(passwordEncoder.matches(TEST_MANAGER_PASSWORD, user.getPassword())).isTrue();
        assertThat(user.getAuthorities())
                .hasSize(2)
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_WAREHOUSE_MANAGER", "ROLE_SALES");
    }
    
    @Test
    @DisplayName("Should verify manager does not have ADMIN role")
    void shouldVerifyManagerDoesNotHaveAdminRole() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_MANAGER_USERNAME);
        
        // Then
        assertThat(user.getAuthorities())
                .extracting("authority")
                .doesNotContain("ROLE_ADMIN");
    }
    
    // ========== Sales User Tests ==========
    
    @Test
    @DisplayName("Should load sales user successfully")
    void shouldLoadSalesUserSuccessfully() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_SALES_USERNAME);
        
        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(TEST_SALES_USERNAME);
        assertThat(user.getPassword()).isNotEmpty();
        assertThat(passwordEncoder.matches(TEST_SALES_PASSWORD, user.getPassword())).isTrue();
        assertThat(user.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("ROLE_SALES");
    }
    
    @Test
    @DisplayName("Should verify sales has only SALES role")
    void shouldVerifySalesHasOnlySalesRole() {
        // When
        UserDetails user = userDetailsService.loadUserByUsername(TEST_SALES_USERNAME);
        
        // Then
        assertThat(user.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("ROLE_SALES");
    }
    
    // ========== Error Cases Tests ==========
    
    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");
    }
    
    @Test
    @DisplayName("Should throw exception for null username")
    void shouldThrowExceptionForNullUsername() {
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(RuntimeException.class); // Can be NullPointerException or UsernameNotFoundException
    }
    
    @Test
    @DisplayName("Should throw exception for empty username")
    void shouldThrowExceptionForEmptyUsername() {
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(""))
                .isInstanceOf(UsernameNotFoundException.class);
    }
    
    // ========== Password Encoding Tests ==========
    
    @Test
    @DisplayName("Should use BCrypt for password encoding")
    void shouldUseBCryptForPasswordEncoding() {
        // When
        UserDetails admin = userDetailsService.loadUserByUsername(TEST_ADMIN_USERNAME);
        UserDetails manager = userDetailsService.loadUserByUsername(TEST_MANAGER_USERNAME);
        UserDetails sales = userDetailsService.loadUserByUsername(TEST_SALES_USERNAME);
        
        // Then - All passwords should be BCrypt encoded (start with $2a$ or $2b$)
        assertThat(admin.getPassword()).matches("^\\$2[ab]\\$.*");
        assertThat(manager.getPassword()).matches("^\\$2[ab]\\$.*");
        assertThat(sales.getPassword()).matches("^\\$2[ab]\\$.*");
    }
    
    @Test
    @DisplayName("Should have different encoded passwords for different users")
    void shouldHaveDifferentEncodedPasswordsForDifferentUsers() {
        // When
        UserDetails admin = userDetailsService.loadUserByUsername(TEST_ADMIN_USERNAME);
        UserDetails manager = userDetailsService.loadUserByUsername(TEST_MANAGER_USERNAME);
        UserDetails sales = userDetailsService.loadUserByUsername(TEST_SALES_USERNAME);
        
        // Then - Each user should have a unique encoded password
        assertThat(admin.getPassword()).isNotEqualTo(manager.getPassword());
        assertThat(admin.getPassword()).isNotEqualTo(sales.getPassword());
        assertThat(manager.getPassword()).isNotEqualTo(sales.getPassword());
    }
    
    // ========== Case Sensitivity Tests ==========
    
    @Test
    @DisplayName("Should be case-sensitive for usernames")
    void shouldBeCaseSensitiveForUsernames() {
        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("Admin"))
                .isInstanceOf(UsernameNotFoundException.class);
        
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ADMIN"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
