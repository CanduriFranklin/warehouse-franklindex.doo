package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando o pagamento de um pedido é confirmado.
 * Este evento será enviado para o módulo Warehouse para iniciar a preparação.
 */
public class PagamentoConfirmadoEvent implements StorefrontDomainEvent {

    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final UUID pedidoId;
    private final String numeroPedido;

    public PagamentoConfirmadoEvent(UUID pedidoId, String numeroPedido) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.pedidoId = pedidoId;
        this.numeroPedido = numeroPedido;
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
        return "PagamentoConfirmadoEvent";
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    @Override
    public String toString() {
        return "PagamentoConfirmadoEvent{" +
                "eventoId=" + eventoId +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", ocorridoEm=" + ocorridoEm +
                '}';
    }
}
