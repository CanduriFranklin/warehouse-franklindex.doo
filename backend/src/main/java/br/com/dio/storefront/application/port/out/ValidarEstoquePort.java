package br.com.dio.storefront.application.port.out;

import br.com.dio.storefront.domain.model.Produto;
import java.util.UUID;

/**
 * Port Out para validação de estoque.
 * Será implementado pela infrastructure.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface ValidarEstoquePort {
    
    /**
     * Valida se há estoque suficiente para o produto.
     * 
     * @param produto Produto a validar
     * @param quantidade Quantidade solicitada
     * @return true se há estoque suficiente
     */
    boolean temEstoqueSuficiente(Produto produto, int quantidade);
    
    /**
     * Reserva estoque para um produto (para pedido pendente).
     * 
     * @param produtoId ID do produto
     * @param quantidade Quantidade a reservar
     */
    void reservarEstoque(UUID produtoId, int quantidade);
    
    /**
     * Libera estoque reservado (em caso de cancelamento).
     * 
     * @param produtoId ID do produto
     * @param quantidade Quantidade a liberar
     */
    void liberarEstoque(UUID produtoId, int quantidade);
}
