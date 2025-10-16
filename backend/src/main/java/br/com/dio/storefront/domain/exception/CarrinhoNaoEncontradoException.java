package br.com.dio.storefront.domain.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um carrinho não é encontrado.
 */
public class CarrinhoNaoEncontradoException extends StorefrontDomainException {

    public CarrinhoNaoEncontradoException(UUID id) {
        super("Carrinho não encontrado com ID: " + id);
    }

    public CarrinhoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
