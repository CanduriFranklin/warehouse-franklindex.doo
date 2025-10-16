package br.com.dio.storefront.infrastructure.persistence;

import br.com.dio.storefront.domain.model.Produto;
import br.com.dio.storefront.domain.repository.ProdutoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository para Produto.
 * Implementa automaticamente ProdutoRepository usando naming conventions.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Repository
public interface JpaProdutoRepository extends JpaRepository<Produto, java.util.UUID>, ProdutoRepository {
    // Spring Data JPA implementa automaticamente todos os métodos
    // definidos em ProdutoRepository usando naming conventions
}
