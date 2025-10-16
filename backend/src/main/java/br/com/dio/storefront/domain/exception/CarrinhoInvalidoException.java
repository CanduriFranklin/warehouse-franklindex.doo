package br.com.dio.storefront.domain.exception;

/**
 * Exceção lançada quando há uma operação inválida no carrinho.
 */
public class CarrinhoInvalidoException extends StorefrontDomainException {

    public CarrinhoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public CarrinhoInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
