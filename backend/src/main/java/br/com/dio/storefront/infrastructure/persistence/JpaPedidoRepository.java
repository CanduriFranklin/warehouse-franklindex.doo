package br.com.dio.storefront.infrastructure.persistence;

import br.com.dio.storefront.domain.model.Pedido;
import br.com.dio.storefront.domain.repository.PedidoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para Pedido.
 * Implementa automaticamente PedidoRepository usando naming conventions.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Repository
public interface JpaPedidoRepository extends JpaRepository<Pedido, UUID>, PedidoRepository {
    // Spring Data JPA implementa automaticamente todos os métodos
}
