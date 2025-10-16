package br.com.dio.storefront.application.port.in;

import java.util.UUID;

/**
 * Use Case para atualizar a quantidade de um produto no carrinho.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface AtualizarQuantidadeCarrinhoUseCase {
    
    /**
     * Atualiza a quantidade de um produto no carrinho.
     * Se a quantidade for zero, remove o produto.
     * 
     * @param command Comando com os dados necessários
     * @throws br.com.dio.storefront.domain.exception.CarrinhoNaoEncontradoException se carrinho não existe
     * @throws br.com.dio.storefront.domain.exception.EstoqueInsuficienteException se estoque insuficiente
     */
    void atualizar(AtualizarQuantidadeCommand command);
    
    /**
     * Comando para atualizar quantidade no carrinho.
     * 
     * @param clienteId ID do cliente
     * @param produtoId ID do produto
     * @param novaQuantidade Nova quantidade (se zero, remove o produto)
     */
    record AtualizarQuantidadeCommand(
        UUID clienteId,
        UUID produtoId,
        Integer novaQuantidade
    ) {
        public AtualizarQuantidadeCommand {
            if (clienteId == null) {
                throw new IllegalArgumentException("ID do cliente é obrigatório");
            }
            if (produtoId == null) {
                throw new IllegalArgumentException("ID do produto é obrigatório");
            }
            if (novaQuantidade == null || novaQuantidade < 0) {
                throw new IllegalArgumentException("Quantidade não pode ser negativa");
            }
        }
    }
}
