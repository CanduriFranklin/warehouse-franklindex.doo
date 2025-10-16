package br.com.dio.storefront.domain.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um pedido não é encontrado.
 */
public class PedidoNaoEncontradoException extends StorefrontDomainException {

    public PedidoNaoEncontradoException(UUID id) {
        super("Pedido não encontrado com ID: " + id);
    }

    public PedidoNaoEncontradoException(String numeroPedido) {
        super("Pedido não encontrado com número: " + numeroPedido);
    }
}
