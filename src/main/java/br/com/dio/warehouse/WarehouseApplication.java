package br.com.dio.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Warehouse Microservice Main Application
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class WarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
