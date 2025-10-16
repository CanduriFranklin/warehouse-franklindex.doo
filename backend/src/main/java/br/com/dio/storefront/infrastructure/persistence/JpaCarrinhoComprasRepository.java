package br.com.dio.storefront.infrastructure.persistence;

import br.com.dio.storefront.domain.model.CarrinhoCompras;
import br.com.dio.storefront.domain.repository.CarrinhoComprasRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository para CarrinhoCompras.
 * Implementa automaticamente CarrinhoComprasRepository usando naming conventions.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Repository
public interface JpaCarrinhoComprasRepository extends JpaRepository<CarrinhoCompras, UUID>, CarrinhoComprasRepository {
    // Spring Data JPA implementa automaticamente todos os métodos
}
