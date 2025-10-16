package br.com.dio.storefront.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para Storefront.
 * Define exchanges, queues e bindings para comunicação assíncrona.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${rabbitmq.exchange.storefront:storefront.events}")
    private String storefrontExchange;
    
    @Value("${rabbitmq.queue.pedidos:storefront.pedidos}")
    private String pedidosQueue;
    
    /**
     * Exchange para eventos do Storefront (topic).
     * Permite routing keys flexíveis: storefront.pedido.criado, storefront.carrinho.finalizado, etc.
     */
    @Bean
    public TopicExchange storefrontEventsExchange() {
        return new TopicExchange(storefrontExchange, true, false);
    }
    
    /**
     * Queue para pedidos criados.
     * Warehouse module irá consumir desta queue.
     */
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(pedidosQueue)
                .withArgument("x-dead-letter-exchange", storefrontExchange + ".dlx")
                .build();
    }
    
    /**
     * Binding: pedidos queue escuta eventos de pedido criado.
     */
    @Bean
    public Binding pedidosBinding(Queue pedidosQueue, TopicExchange storefrontEventsExchange) {
        return BindingBuilder
                .bind(pedidosQueue)
                .to(storefrontEventsExchange)
                .with("storefront.pedido.criado");
    }
    
    /**
     * Dead Letter Exchange para mensagens com falha.
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(storefrontExchange + ".dlx", true, false);
    }
    
    /**
     * Dead Letter Queue.
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(storefrontExchange + ".dlq").build();
    }
    
    /**
     * Binding DLQ.
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("");
    }
    
    /**
     * RabbitTemplate com Jackson2JsonMessageConverter.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
    
    /**
     * Converter para serialização JSON.
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
