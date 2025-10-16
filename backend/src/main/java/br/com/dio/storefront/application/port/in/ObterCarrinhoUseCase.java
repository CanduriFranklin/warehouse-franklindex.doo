package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.CarrinhoCompras;
import java.util.UUID;

/**
 * Use Case para obter o carrinho de compras do cliente.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface ObterCarrinhoUseCase {
    
    /**
     * Obtém o carrinho ativo do cliente.
     * Se o cliente não tem carrinho ativo, cria um novo.
     * 
     * @param clienteId ID do cliente
     * @return Carrinho de compras do cliente
     * @throws br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException se cliente não existe
     */
    CarrinhoCompras obter(UUID clienteId);
}
