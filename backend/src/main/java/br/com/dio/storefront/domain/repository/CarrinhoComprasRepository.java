package br.com.dio.storefront.domain.repository;

import br.com.dio.storefront.domain.model.CarrinhoCompras;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para CarrinhoCompras (Domain Layer).
 * Define o contrato para persistência de carrinhos de compras.
 * Usa naming conventions do Spring Data JPA.
 */
public interface CarrinhoComprasRepository {

    /**
     * Salva um carrinho de compras.
     */
    CarrinhoCompras save(CarrinhoCompras carrinho);

    /**
     * Busca um carrinho por ID.
     */
    Optional<CarrinhoCompras> findById(UUID id);

    /**
     * Busca um carrinho de um cliente por status.
     */
    Optional<CarrinhoCompras> findByClienteIdAndStatus(
            UUID clienteId, 
            CarrinhoCompras.StatusCarrinho status);

    /**
     * Verifica se um carrinho existe.
     */
    boolean existsById(UUID id);

    /**
     * Remove um carrinho.
     */
    void deleteById(UUID id);
}
