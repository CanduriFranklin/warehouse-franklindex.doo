package br.com.dio.storefront.domain.exception;

/**
 * Exceção lançada quando há uma operação inválida em um pedido.
 */
public class PedidoInvalidoException extends StorefrontDomainException {

    public PedidoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public PedidoInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
