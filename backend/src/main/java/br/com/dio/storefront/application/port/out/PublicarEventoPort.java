package br.com.dio.storefront.application.port.out;

import br.com.dio.storefront.domain.event.StorefrontDomainEvent;

/**
 * Port Out para publicação de Domain Events.
 * Será implementado pela infrastructure usando RabbitMQ.
 * 
 * Hexagonal Architecture:
 * - Port Out: Interface para saída do sistema
 * - Driven Side: Implementado pela infrastructure
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface PublicarEventoPort {
    
    /**
     * Publica um domain event no message broker (RabbitMQ).
     * 
     * @param evento Evento a ser publicado
     */
    void publicar(StorefrontDomainEvent evento);
}
