package br.com.dio.storefront.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe base abstrata para todos os Domain Events do Storefront.
 * 
 * Princípios DDD:
 * - Domain Event: Representa algo que aconteceu no domínio
 * - Imutabilidade: Eventos não podem ser modificados
 * - Rastreabilidade: Todos eventos têm ID e timestamp
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public abstract class DomainEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final String tipoEvento;
    
    /**
     * Construtor protegido para subclasses.
     * 
     * @param tipoEvento Tipo do evento (nome da classe)
     */
    protected DomainEvent(String tipoEvento) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.tipoEvento = tipoEvento;
    }
    
    public UUID getEventoId() {
        return eventoId;
    }
    
    public LocalDateTime getOcorridoEm() {
        return ocorridoEm;
    }
    
    public String getTipoEvento() {
        return tipoEvento;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEvent that = (DomainEvent) o;
        return Objects.equals(eventoId, that.eventoId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventoId);
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventoId=%s, ocorridoEm=%s}",
            tipoEvento, eventoId, ocorridoEm);
    }
}
