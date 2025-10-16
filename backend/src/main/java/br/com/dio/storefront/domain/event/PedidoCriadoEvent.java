package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio disparado quando um pedido é criado.
 * Este evento será enviado para o módulo Warehouse via RabbitMQ.
 */
public class PedidoCriadoEvent implements StorefrontDomainEvent {

    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final UUID pedidoId;
    private final String numeroPedido;
    private final UUID clienteId;
    private final String nomeCliente;
    private final String emailCliente;

    public PedidoCriadoEvent(UUID pedidoId, String numeroPedido, UUID clienteId, 
                             String nomeCliente, String emailCliente) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.pedidoId = pedidoId;
        this.numeroPedido = numeroPedido;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
        this.emailCliente = emailCliente;
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
        return "PedidoCriadoEvent";
    }

    public UUID getPedidoId() {
        return pedidoId;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    @Override
    public String toString() {
        return "PedidoCriadoEvent{" +
                "eventoId=" + eventoId +
                ", numeroPedido='" + numeroPedido + '\'' +
                ", clienteId=" + clienteId +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", ocorridoEm=" + ocorridoEm +
                '}';
    }
}
