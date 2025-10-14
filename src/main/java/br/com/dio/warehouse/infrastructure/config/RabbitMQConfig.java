package br.com.dio.warehouse.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ Configuration
 * 
 * Topology:
 * - Exchange: warehouse.events (topic)
 * - Queues: warehouse.delivery, warehouse.baskets.sold, warehouse.baskets.disposed
 * - Dead Letter Exchange: warehouse.dlx
 * - Dead Letter Queue: warehouse.dlq
 * 
 * Routing Keys:
 * - delivery.received → warehouse.delivery
 * - baskets.sold → warehouse.baskets.sold
 * - baskets.disposed → warehouse.baskets.disposed
 * 
 * Features:
 * - Automatic retry with exponential backoff
 * - Dead Letter Queue for failed messages
 * - JSON serialization with Jackson
 * - Connection recovery
 * 
 * @author Franklin Canduri
 * @version 1.0.0
 * @since 2025
 */
@Slf4j
@Configuration
public class RabbitMQConfig {
    
    // Exchange names
    public static final String EVENTS_EXCHANGE = "warehouse.events";
    public static final String DLX_EXCHANGE = "warehouse.dlx";
    
    // Queue names
    public static final String DELIVERY_QUEUE = "warehouse.delivery";
    public static final String BASKETS_SOLD_QUEUE = "warehouse.baskets.sold";
    public static final String BASKETS_DISPOSED_QUEUE = "warehouse.baskets.disposed";
    public static final String DLQ = "warehouse.dlq";
    
    // Routing keys
    public static final String DELIVERY_ROUTING_KEY = "delivery.received";
    public static final String BASKETS_SOLD_ROUTING_KEY = "baskets.sold";
    public static final String BASKETS_DISPOSED_ROUTING_KEY = "baskets.disposed";
    
    @Value("${spring.rabbitmq.listener.simple.retry.enabled:true}")
    private boolean retryEnabled;
    
    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts:3}")
    private int maxAttempts;
    
    @Value("${spring.rabbitmq.listener.simple.retry.initial-interval:3000}")
    private long initialInterval;
    
    @Value("${spring.rabbitmq.listener.simple.retry.multiplier:2.0}")
    private double multiplier;
    
    // ========== Message Converter ==========
    
    /**
     * Jackson message converter for JSON serialization
     * Configured to handle Java 8 time types and pretty print
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        
        log.info("✅ Configured Jackson JSON message converter for RabbitMQ");
        return converter;
    }
    
    /**
     * RabbitTemplate with JSON converter and connection recovery
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setMandatory(true); // Enable return callback
        
        // Confirm callback (publisher confirms)
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("✅ Message confirmed: {}", correlationData);
            } else {
                log.error("❌ Message not confirmed: {} - Cause: {}", correlationData, cause);
            }
        });
        
        // Return callback (for unroutable messages)
        template.setReturnsCallback(returned -> {
            log.error("❌ Message returned: {} - Reply: {} - Exchange: {} - Routing Key: {}",
                    returned.getMessage(),
                    returned.getReplyText(),
                    returned.getExchange(),
                    returned.getRoutingKey());
        });
        
        log.info("✅ Configured RabbitTemplate with callbacks");
        return template;
    }
    
    // ========== Exchanges ==========
    
    /**
     * Main events exchange (topic)
     * All domain events are published here
     */
    @Bean
    public TopicExchange eventsExchange() {
        TopicExchange exchange = new TopicExchange(EVENTS_EXCHANGE, true, false);
        log.info("📡 Created events exchange: {}", EVENTS_EXCHANGE);
        return exchange;
    }
    
    /**
     * Dead Letter Exchange (DLX)
     * Failed messages are routed here after max retry attempts
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        DirectExchange exchange = new DirectExchange(DLX_EXCHANGE, true, false);
        log.info("☠️ Created dead letter exchange: {}", DLX_EXCHANGE);
        return exchange;
    }
    
    // ========== Queues ==========
    
    /**
     * Delivery events queue
     * Consumes: DeliveryReceivedEvent
     */
    @Bean
    public Queue deliveryQueue() {
        Map<String, Object> args = deadLetterArgs();
        Queue queue = new Queue(DELIVERY_QUEUE, true, false, false, args);
        log.info("📬 Created delivery queue: {}", DELIVERY_QUEUE);
        return queue;
    }
    
    /**
     * Baskets sold events queue
     * Consumes: BasketsSoldEvent
     */
    @Bean
    public Queue basketsSoldQueue() {
        Map<String, Object> args = deadLetterArgs();
        Queue queue = new Queue(BASKETS_SOLD_QUEUE, true, false, false, args);
        log.info("📬 Created baskets sold queue: {}", BASKETS_SOLD_QUEUE);
        return queue;
    }
    
    /**
     * Baskets disposed events queue
     * Consumes: BasketsDisposedEvent
     */
    @Bean
    public Queue basketsDisposedQueue() {
        Map<String, Object> args = deadLetterArgs();
        Queue queue = new Queue(BASKETS_DISPOSED_QUEUE, true, false, false, args);
        log.info("📬 Created baskets disposed queue: {}", BASKETS_DISPOSED_QUEUE);
        return queue;
    }
    
    /**
     * Dead Letter Queue (DLQ)
     * Stores failed messages for manual inspection
     */
    @Bean
    public Queue deadLetterQueue() {
        Queue queue = new Queue(DLQ, true, false, false);
        log.info("☠️ Created dead letter queue: {}", DLQ);
        return queue;
    }
    
    /**
     * Dead letter arguments for queue configuration
     * Routes failed messages to DLX after max retries
     */
    private Map<String, Object> deadLetterArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLQ);
        return args;
    }
    
    // ========== Bindings ==========
    
    /**
     * Bind delivery queue to events exchange
     * Routing key: delivery.received
     */
    @Bean
    public Binding deliveryBinding(Queue deliveryQueue, TopicExchange eventsExchange) {
        Binding binding = BindingBuilder
                .bind(deliveryQueue)
                .to(eventsExchange)
                .with(DELIVERY_ROUTING_KEY);
        log.info("🔗 Bound {} to {} with routing key: {}", 
                DELIVERY_QUEUE, EVENTS_EXCHANGE, DELIVERY_ROUTING_KEY);
        return binding;
    }
    
    /**
     * Bind baskets sold queue to events exchange
     * Routing key: baskets.sold
     */
    @Bean
    public Binding basketsSoldBinding(Queue basketsSoldQueue, TopicExchange eventsExchange) {
        Binding binding = BindingBuilder
                .bind(basketsSoldQueue)
                .to(eventsExchange)
                .with(BASKETS_SOLD_ROUTING_KEY);
        log.info("🔗 Bound {} to {} with routing key: {}", 
                BASKETS_SOLD_QUEUE, EVENTS_EXCHANGE, BASKETS_SOLD_ROUTING_KEY);
        return binding;
    }
    
    /**
     * Bind baskets disposed queue to events exchange
     * Routing key: baskets.disposed
     */
    @Bean
    public Binding basketsDisposedBinding(Queue basketsDisposedQueue, TopicExchange eventsExchange) {
        Binding binding = BindingBuilder
                .bind(basketsDisposedQueue)
                .to(eventsExchange)
                .with(BASKETS_DISPOSED_ROUTING_KEY);
        log.info("🔗 Bound {} to {} with routing key: {}", 
                BASKETS_DISPOSED_QUEUE, EVENTS_EXCHANGE, BASKETS_DISPOSED_ROUTING_KEY);
        return binding;
    }
    
    /**
     * Bind dead letter queue to DLX
     * All failed messages go here
     */
    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        Binding binding = BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ);
        log.info("🔗 Bound {} to {}", DLQ, DLX_EXCHANGE);
        return binding;
    }
    
    // ========== Listener Container Factory ==========
    
    /**
     * Custom listener container factory with retry configuration
     * Enables automatic retry with exponential backoff
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        
        if (retryEnabled) {
            factory.setAdviceChain(retryInterceptor());
            log.info("✅ Enabled retry with {} max attempts, initial interval {}ms, multiplier {}", 
                    maxAttempts, initialInterval, multiplier);
        }
        
        log.info("✅ Configured RabbitListenerContainerFactory");
        return factory;
    }
    
    /**
     * Retry interceptor with exponential backoff
     * After max attempts, messages are sent to DLQ
     */
    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(maxAttempts)
                .backOffOptions(initialInterval, multiplier, initialInterval * 10)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}
