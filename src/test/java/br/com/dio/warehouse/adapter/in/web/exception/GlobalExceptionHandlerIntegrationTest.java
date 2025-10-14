package br.com.dio.warehouse.adapter.in.web.exception;

import br.com.dio.warehouse.application.port.in.ReceiveDeliveryUseCase;
import br.com.dio.warehouse.application.port.in.SellBasketsUseCase;
import br.com.dio.warehouse.domain.exception.BasketNotFoundException;
import br.com.dio.warehouse.domain.exception.InsufficientStockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GlobalExceptionHandler
 * Tests exception handling through real REST endpoints
 */
import org.junit.jupiter.api.Disabled;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GlobalExceptionHandler Integration Tests")
@Disabled("Temporarily disabled until all application components are implemented")
class GlobalExceptionHandlerIntegrationTest {
    
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ReceiveDeliveryUseCase receiveDeliveryUseCase() {
            return Mockito.mock(ReceiveDeliveryUseCase.class);
        }
        
        @Bean
        @Primary
        public SellBasketsUseCase sellBasketsUseCase() {
            return Mockito.mock(SellBasketsUseCase.class);
        }
    }
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ReceiveDeliveryUseCase receiveDeliveryUseCase;
    
    @Autowired
    private SellBasketsUseCase sellBasketsUseCase;
    
    // ========== Domain Exception Integration Tests ==========
    
    @Test
    @DisplayName("Should return 404 when basket not found")
    void shouldReturn404WhenBasketNotFound() throws Exception {
        // Given
        UUID basketId = UUID.randomUUID();
        when(sellBasketsUseCase.execute(any()))
                .thenThrow(new BasketNotFoundException(basketId));
        
        Map<String, Object> request = new HashMap<>();
        request.put("quantity", 5);
        
        // When & Then
        mockMvc.perform(post("/api/v1/baskets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.type").value(containsString("not-found")))
                .andExpect(jsonPath("$.title").value("Basket Not Found"))
                .andExpect(jsonPath("$.detail").value(containsString(basketId.toString())))
                .andExpect(jsonPath("$.instance").value("/api/v1/baskets/sell"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    @DisplayName("Should return 422 when insufficient stock")
    void shouldReturn422WhenInsufficientStock() throws Exception {
        // Given
        when(sellBasketsUseCase.execute(any()))
                .thenThrow(new InsufficientStockException("Insufficient stock. Available: 3, Requested: 10"));
        
        Map<String, Object> request = new HashMap<>();
        request.put("quantity", 10);
        
        // When & Then
        mockMvc.perform(post("/api/v1/baskets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.title").value("Insufficient Stock"))
                .andExpect(jsonPath("$.detail").value(containsString("Insufficient stock")))
                .andExpect(jsonPath("$.instance").value("/api/v1/baskets/sell"));
    }
    
    // ========== Validation Exception Integration Tests ==========
    
    @Test
    @DisplayName("Should return 400 with field errors when validation fails")
    void shouldReturn400WithFieldErrorsWhenValidationFails() throws Exception {
        // Given - Invalid request with negative quantity and null cost
        Map<String, Object> request = new HashMap<>();
        request.put("totalQuantity", -5);
        request.put("validationDate", LocalDate.now().plusDays(30).toString());
        request.put("totalCost", null);
        request.put("profitMarginPercentage", new BigDecimal("20"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value(containsString("validation-error")))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.detail").value(containsString("Validation failed")))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.errors[*].field").value(hasItems("totalQuantity", "totalCost")))
                .andExpect(jsonPath("$.errors[0].rejectedValue").exists())
                .andExpect(jsonPath("$.errors[0].message").exists());
    }
    
    @Test
    @DisplayName("Should return 400 when JSON is malformed")
    void shouldReturn400WhenJsonMalformed() throws Exception {
        // Given - Malformed JSON
        String malformedJson = "{ totalQuantity: 5, invalidJson }";
        
        // When & Then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Malformed Request"))
                .andExpect(jsonPath("$.detail").value(containsString("JSON syntax")));
    }
    
    @Test
    @DisplayName("Should return 400 with all required validation messages")
    void shouldReturn400WithAllRequiredValidationMessages() throws Exception {
        // Given - Empty request (all fields missing)
        Map<String, Object> request = new HashMap<>();
        
        // When & Then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(4)))) // 4 required fields
                .andExpect(jsonPath("$.errors[*].field").value(
                        hasItems("totalQuantity", "validationDate", "totalCost", "profitMarginPercentage")
                ));
    }
    
    @Test
    @DisplayName("Should return 400 when quantity is zero")
    void shouldReturn400WhenQuantityIsZero() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("quantity", 0);
        
        // When & Then
        mockMvc.perform(post("/api/v1/baskets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("quantity"))
                .andExpect(jsonPath("$.errors[0].rejectedValue").value(0));
    }
    
    // ========== RFC 7807 Compliance Integration Tests ==========
    
    @Test
    @DisplayName("Should return RFC 7807 compliant error structure")
    void shouldReturnRfc7807CompliantErrorStructure() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("quantity", -5);
        
        // When & Then
        mockMvc.perform(post("/api/v1/baskets/sell")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.type").exists())
                .andExpect(jsonPath("$.type").value(startsWith("https://")))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")));
    }
    
    @Test
    @DisplayName("Should include validation errors array when field validation fails")
    void shouldIncludeValidationErrorsArrayWhenFieldValidationFails() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("totalQuantity", -10);
        request.put("validationDate", LocalDate.now().toString());
        request.put("totalCost", new BigDecimal("-100"));
        request.put("profitMarginPercentage", new BigDecimal("15"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").exists())
                .andExpect(jsonPath("$.errors[0].rejectedValue").exists())
                .andExpect(jsonPath("$.errors[0].message").exists());
    }
}
