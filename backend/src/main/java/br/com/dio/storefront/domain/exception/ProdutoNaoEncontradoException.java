package br.com.dio.storefront.domain.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um produto não é encontrado.
 */
public class ProdutoNaoEncontradoException extends StorefrontDomainException {

    public ProdutoNaoEncontradoException(UUID id) {
        super("Produto não encontrado com ID: " + id);
    }

    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
