package br.com.dio.storefront.domain.exception;

/**
 * Exceção lançada quando há problemas com estoque de produto.
 */
public class EstoqueInsuficienteException extends StorefrontDomainException {

    public EstoqueInsuficienteException(String nomeProduto, int quantidadeSolicitada, int quantidadeDisponivel) {
        super(String.format("Estoque insuficiente para o produto '%s'. Solicitado: %d, Disponível: %d",
                nomeProduto, quantidadeSolicitada, quantidadeDisponivel));
    }

    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
