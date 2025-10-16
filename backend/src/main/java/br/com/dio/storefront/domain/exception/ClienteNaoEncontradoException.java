package br.com.dio.storefront.domain.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um cliente não é encontrado.
 */
public class ClienteNaoEncontradoException extends StorefrontDomainException {

    public ClienteNaoEncontradoException(UUID id) {
        super("Cliente não encontrado com ID: " + id);
    }

    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
