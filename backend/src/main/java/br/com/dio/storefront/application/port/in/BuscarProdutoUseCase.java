package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.Produto;
import java.util.UUID;

/**
 * Use Case para buscar um produto específico.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface BuscarProdutoUseCase {
    
    /**
     * Busca um produto por ID.
     * 
     * @param produtoId ID do produto
     * @return Produto encontrado
     * @throws br.com.dio.storefront.domain.exception.ProdutoNaoEncontradoException se produto não existe
     */
    Produto buscar(UUID produtoId);
}
