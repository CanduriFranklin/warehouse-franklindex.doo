package br.com.dio.storefront.domain.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain Event emitido quando um cliente é cadastrado.
 * Útil para notificações de boas-vindas e integração com outros sistemas.
 * 
 * Uso:
 * - Email: Enviar mensagem de boas-vindas
 * - CRM: Registrar novo cliente em sistema externo
 * - Analytics: Métricas de crescimento de base de clientes
 * - Gamificação: Atribuir pontos ou benefícios para novos clientes
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public class ClienteCadastradoEvent implements StorefrontDomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventoId;
    private final LocalDateTime ocorridoEm;
    private final String tipo;
    private final UUID clienteId;
    private final String nomeCompleto;
    private final String email;
    private final String cpf;
    
    /**
     * Construtor do evento.
     * 
     * @param clienteId ID do cliente cadastrado
     * @param nomeCompleto Nome completo do cliente
     * @param email Email do cliente
     * @param cpf CPF do cliente
     */
    public ClienteCadastradoEvent(UUID clienteId, String nomeCompleto, String email, String cpf) {
        this.eventoId = UUID.randomUUID();
        this.ocorridoEm = LocalDateTime.now();
        this.tipo = "ClienteCadastradoEvent";
        this.clienteId = clienteId;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.cpf = cpf;
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
    
    public UUID getClienteId() {
        return clienteId;
    }
    
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteCadastradoEvent that = (ClienteCadastradoEvent) o;
        return Objects.equals(eventoId, that.eventoId) &&
               Objects.equals(clienteId, that.clienteId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, clienteId);
    }
    
    @Override
    public String toString() {
        return String.format(
            "ClienteCadastradoEvent{clienteId=%s, nome='%s', email='%s', ocorridoEm=%s}",
            clienteId, nomeCompleto, email, getOcorridoEm()
        );
    }
}
