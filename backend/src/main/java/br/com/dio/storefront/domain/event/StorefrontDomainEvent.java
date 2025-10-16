package br.com.dio.storefront.domain.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interface para eventos do domínio Storefront.
 */
public interface StorefrontDomainEvent extends Serializable {
    UUID getEventoId();
    LocalDateTime getOcorridoEm();
    String getTipo();
}
