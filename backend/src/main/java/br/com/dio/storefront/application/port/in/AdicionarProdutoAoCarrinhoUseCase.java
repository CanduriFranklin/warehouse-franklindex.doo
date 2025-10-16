package br.com.dio.storefront.application.port.in;

import java.util.UUID;

/**
 * Use Case para adicionar produtos ao carrinho de compras.
 * 
 * Representa a intenção do usuário de adicionar um item ao seu carrinho.
 * 
 * Hexagonal Architecture:
 * - Port In: Interface que define entrada no sistema
 * - Driving Side: Chamado por Controllers REST ou Messaging
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface AdicionarProdutoAoCarrinhoUseCase {
    
    /**
     * Adiciona um produto ao carrinho do cliente.
     * Se o produto já existe no carrinho, incrementa a quantidade.
     * 
     * @param command Comando com os dados necessários
     * @return ID do carrinho atualizado
     * @throws br.com.dio.storefront.domain.exception.ProdutoNaoEncontradoException se produto não existe
     * @throws br.com.dio.storefront.domain.exception.EstoqueInsuficienteException se estoque insuficiente
     * @throws br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException se cliente não existe
     */
    UUID adicionar(AdicionarProdutoCommand command);
    
    /**
     * Comando para adicionar produto ao carrinho.
     * 
     * @param clienteId ID do cliente
     * @param produtoId ID do produto
     * @param quantidade Quantidade a adicionar
     */
    record AdicionarProdutoCommand(
        UUID clienteId,
        UUID produtoId,
        Integer quantidade
    ) {
        public AdicionarProdutoCommand {
            if (clienteId == null) {
                throw new IllegalArgumentException("ID do cliente é obrigatório");
            }
            if (produtoId == null) {
                throw new IllegalArgumentException("ID do produto é obrigatório");
            }
            if (quantidade == null || quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            }
        }
    }
}
