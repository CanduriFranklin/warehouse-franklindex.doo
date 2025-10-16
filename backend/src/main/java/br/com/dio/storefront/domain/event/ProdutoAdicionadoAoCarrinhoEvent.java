package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain Event emitido quando um produto é adicionado ao carrinho.
 * Útil para analytics, recomendações e detecção de carrinhos abandonados.
 * 
 * Uso:
 * - Analytics: Produtos mais adicionados ao carrinho
 * - Recomendações: "Clientes que compraram X também compraram Y"
 * - Retargeting: Enviar email sobre carrinho abandonado
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public class ProdutoAdicionadoAoCarrinhoEvent implements StorefrontDomainEvent {

    private static final long serialVersionUID = 1L;
    
    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final String tipo;
    private final UUID carrinhoId;
    private final UUID clienteId;
    private final UUID produtoId;
    private final String nomeProduto;
    private final int quantidade;
    
    /**
     * Construtor do evento.
     * 
     * @param carrinhoId ID do carrinho
     * @param clienteId ID do cliente
     * @param produtoId ID do produto
     * @param nomeProduto Nome do produto
     * @param quantidade Quantidade adicionada
     */
    public ProdutoAdicionadoAoCarrinhoEvent(UUID carrinhoId, UUID clienteId,
                                           UUID produtoId, String nomeProduto, int quantidade) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.tipo = "ProdutoAdicionadoAoCarrinhoEvent";
        this.carrinhoId = carrinhoId;
        this.clienteId = clienteId;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
    }
    
    @Override
    public UUID getEventoId() {
        return eventoId;
    }
    
    @Override
    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }
    
    @Override
    public String getTipo() {
        return tipo;
    }
    
    public UUID getCarrinhoId() {
        return carrinhoId;
    }
    
    public UUID getClienteId() {
        return clienteId;
    }
    
    public UUID getProdutoId() {
        return produtoId;
    }
    
    public String getNomeProduto() {
        return nomeProduto;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProdutoAdicionadoAoCarrinhoEvent that = (ProdutoAdicionadoAoCarrinhoEvent) o;
        return quantidade == that.quantidade &&
               Objects.equals(eventoId, that.eventoId) &&
               Objects.equals(carrinhoId, that.carrinhoId) &&
               Objects.equals(clienteId, that.clienteId) &&
               Objects.equals(produtoId, that.produtoId) &&
               Objects.equals(nomeProduto, that.nomeProduto);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, carrinhoId, clienteId, produtoId, nomeProduto, quantidade);
    }
    
    @Override
    public String toString() {
        return String.format(
            "ProdutoAdicionadoAoCarrinhoEvent{carrinhoId=%s, clienteId=%s, produto='%s', quantidade=%d, ocorridoEm=%s}",
            carrinhoId, clienteId, nomeProduto, quantidade, getOcorridoEm()
        );
    }
}
