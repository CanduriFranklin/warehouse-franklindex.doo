package br.com.dio.storefront.application.port.in;

import java.util.UUID;

/**
 * Use Case para remover produtos do carrinho de compras.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface RemoverProdutoDoCarrinhoUseCase {
    
    /**
     * Remove um produto do carrinho do cliente.
     * 
     * @param command Comando com os dados necessários
     * @throws br.com.dio.storefront.domain.exception.CarrinhoNaoEncontradoException se carrinho não existe
     * @throws br.com.dio.storefront.domain.exception.ProdutoNaoEncontradoException se produto não está no carrinho
     */
    void remover(RemoverProdutoCommand command);
    
    /**
     * Comando para remover produto do carrinho.
     * 
     * @param clienteId ID do cliente
     * @param produtoId ID do produto a remover
     */
    record RemoverProdutoCommand(
        UUID clienteId,
        UUID produtoId
    ) {
        public RemoverProdutoCommand {
            if (clienteId == null) {
                throw new IllegalArgumentException("ID do cliente é obrigatório");
            }
            if (produtoId == null) {
                throw new IllegalArgumentException("ID do produto é obrigatório");
            }
        }
    }
}
