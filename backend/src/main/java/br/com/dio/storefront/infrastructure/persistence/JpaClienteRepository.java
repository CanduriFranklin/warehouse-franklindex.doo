package br.com.dio.storefront.infrastructure.persistence;

import br.com.dio.storefront.domain.model.Cliente;
import br.com.dio.storefront.domain.repository.ClienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para Cliente.
 * Implementa automaticamente ClienteRepository usando naming conventions.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Repository
public interface JpaClienteRepository extends JpaRepository<Cliente, UUID>, ClienteRepository {
    // Spring Data JPA implementa automaticamente todos os métodos
}
