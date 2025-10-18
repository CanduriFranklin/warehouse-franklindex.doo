package br.com.dio.storefront.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.dio.storefront.application.port.out.PublicarEventoPort;
import br.com.dio.storefront.domain.event.StorefrontDomainEvent;

/**
 * Implementação de PublicarEventoPort usando RabbitMQ.
 * Publica Domain Events para message broker.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Component("storefrontEventPublisher")
public class RabbitMQEventPublisher implements PublicarEventoPort {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${rabbitmq.exchange.storefront:storefront.events}")
    private String exchange;
    
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public void publicar(StorefrontDomainEvent evento) {
        try {
            String routingKey = gerarRoutingKey(evento);
            String mensagemJson = objectMapper.writeValueAsString(evento);
            
            rabbitTemplate.convertAndSend(exchange, routingKey, mensagemJson);
            
            logger.info("Evento publicado: {} com routing key: {}", 
                    evento.getClass().getSimpleName(), routingKey);
            
        } catch (JsonProcessingException e) {
            logger.error("Erro ao serializar evento: {}", evento.getClass().getSimpleName(), e);
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }
    
    /**
     * Gera routing key baseado no tipo do evento.
     * Exemplo: PedidoCriadoEvent -> storefront.pedido.criado
     */
    private String gerarRoutingKey(StorefrontDomainEvent evento) {
        String className = evento.getClass().getSimpleName();
        String eventName = className
                .replace("Event", "")
                .replaceAll("([a-z])([A-Z])", "$1.$2")
                .toLowerCase();
        return "storefront." + eventName;
    }
}
