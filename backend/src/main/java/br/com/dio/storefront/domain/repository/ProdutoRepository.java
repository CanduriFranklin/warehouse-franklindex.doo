package br.com.dio.storefront.domain.repository;

import br.com.dio.storefront.domain.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Produto (Domain Layer).
 * Define o contrato para persistência de produtos.
 * Usa naming conventions do Spring Data JPA.
 */
public interface ProdutoRepository {

    /**
     * Salva um produto.
     */
    Produto save(Produto produto);

    /**
     * Busca um produto por ID.
     */
    Optional<Produto> findById(UUID id);

    /**
     * Busca todos os produtos ativos (paginado).
     */
    Page<Produto> findByAtivoTrue(Pageable pageable);

    /**
     * Busca produtos por categoria (paginado).
     */
    Page<Produto> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);

    /**
     * Busca produtos por nome - busca parcial, case insensitive (paginado).
     */
    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String termo, Pageable pageable);

    /**
     * Busca todos os produtos ativos.
     */
    List<Produto> findAllByAtivoTrue();

    /**
     * Verifica se um produto existe.
     */
    boolean existsById(UUID id);

    /**
     * Remove um produto.
     */
    void deleteById(UUID id);
}
