package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain Event emitido quando um carrinho é finalizado (checkout).
 * Marca o fim do processo de compra no carrinho antes da criação do pedido.
 * 
 * Uso:
 * - Analytics: Taxa de conversão (carrinhos finalizados vs criados)
 * - Métricas: Tempo médio entre criação e finalização do carrinho
 * - Auditoria: Rastreamento do processo de compra
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public class CarrinhoFinalizadoEvent implements StorefrontDomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final String tipo;
    private final UUID carrinhoId;
    private final UUID clienteId;
    private final int quantidadeTotalItens;
    
    /**
     * Construtor do evento.
     * 
     * @param carrinhoId ID do carrinho finalizado
     * @param clienteId ID do cliente
     * @param quantidadeTotalItens Quantidade total de itens no carrinho
     */
    public CarrinhoFinalizadoEvent(UUID carrinhoId, UUID clienteId, int quantidadeTotalItens) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.tipo = "CarrinhoFinalizadoEvent";
        this.carrinhoId = carrinhoId;
        this.clienteId = clienteId;
        this.quantidadeTotalItens = quantidadeTotalItens;
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
    
    public int getQuantidadeTotalItens() {
        return quantidadeTotalItens;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarrinhoFinalizadoEvent that = (CarrinhoFinalizadoEvent) o;
        return Objects.equals(eventoId, that.eventoId) &&
               Objects.equals(carrinhoId, that.carrinhoId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, carrinhoId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "CarrinhoFinalizadoEvent{carrinhoId=%s, clienteId=%s, quantidadeTotalItens=%d, ocorridoEm=%s}",
            carrinhoId, clienteId, quantidadeTotalItens, getOcorridoEm()
        );
    }
}
