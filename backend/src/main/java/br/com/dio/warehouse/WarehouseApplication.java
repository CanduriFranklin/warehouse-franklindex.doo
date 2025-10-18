package br.com.dio.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
@ComponentScan(basePackages = {
    "br.com.dio.warehouse",
    "br.com.dio.storefront"
})
@EnableJpaRepositories(basePackages = {
    "br.com.dio.warehouse.infrastructure.persistence",
    "br.com.dio.storefront.infrastructure.persistence"
})
@EntityScan(basePackages = {
    "br.com.dio.warehouse.domain.model",
    "br.com.dio.storefront.domain.model"
})
public class WarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);
    }
}
