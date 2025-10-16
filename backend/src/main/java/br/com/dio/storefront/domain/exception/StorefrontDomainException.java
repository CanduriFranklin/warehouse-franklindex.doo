package br.com.dio.storefront.domain.exception;

/**
 * Exceção de domínio base para o módulo Storefront.
 */
public abstract class StorefrontDomainException extends RuntimeException {

    public StorefrontDomainException(String mensagem) {
        super(mensagem);
    }

    public StorefrontDomainException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
