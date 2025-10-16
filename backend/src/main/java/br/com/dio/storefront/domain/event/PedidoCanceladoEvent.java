package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando um pedido é cancelado.
 */
public class PedidoCanceladoEvent implements StorefrontDomainEvent {

    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final UUID pedidoId;
    private final String numeroPedido;
    private final String motivo;

    public PedidoCanceladoEvent(UUID pedidoId, String numeroPedido, String motivo) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.pedidoId = pedidoId;
        this.numeroPedido = numeroPedido;
        this.motivo = motivo;
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
        return "PedidoCanceladoEvent";
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return "PedidoCanceladoEvent{" +
                "eventoId=" + eventoId +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", motivo='" + motivo + '\'' +
                ", ocorridoEm=" + ocorridoEm +
                '}';
    }
}
