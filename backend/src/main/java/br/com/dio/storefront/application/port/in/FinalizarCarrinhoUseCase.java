package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.valueobject.Endereco;
import br.com.dio.storefront.domain.valueobject.InformacaoPagamento;
import java.util.UUID;

/**
 * Use Case para finalizar o carrinho (checkout).
 * Este é o passo final antes de criar o pedido.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface FinalizarCarrinhoUseCase {
    
    /**
     * Finaliza o carrinho e cria um pedido.
     * 
     * Fluxo:
     * 1. Valida carrinho e estoque
     * 2. Cria pedido com os dados
     * 3. Finaliza carrinho
     * 4. Emite PedidoCriadoEvent para Warehouse
     * 
     * @param command Comando com dados de entrega e pagamento
     * @return ID do pedido criado
     * @throws br.com.dio.storefront.domain.exception.CarrinhoNaoEncontradoException se carrinho não existe
     * @throws br.com.dio.storefront.domain.exception.CarrinhoInvalidoException se carrinho está vazio
     * @throws br.com.dio.storefront.domain.exception.EstoqueInsuficienteException se estoque insuficiente
     */
    UUID finalizar(FinalizarCarrinhoCommand command);
    
    /**
     * Comando para finalizar carrinho.
     * 
     * @param clienteId ID do cliente
     * @param enderecoEntrega Endereço para entrega
     * @param informacaoPagamento Dados de pagamento
     */
    record FinalizarCarrinhoCommand(
        UUID clienteId,
        Endereco enderecoEntrega,
        InformacaoPagamento informacaoPagamento
    ) {
        public FinalizarCarrinhoCommand {
            if (clienteId == null) {
                throw new IllegalArgumentException("ID do cliente é obrigatório");
            }
            if (enderecoEntrega == null) {
                throw new IllegalArgumentException("Endereço de entrega é obrigatório");
            }
            if (informacaoPagamento == null) {
                throw new IllegalArgumentException("Informação de pagamento é obrigatória");
            }
        }
    }
}
