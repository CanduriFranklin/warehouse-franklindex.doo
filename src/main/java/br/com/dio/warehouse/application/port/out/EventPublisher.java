package br.com.dio.warehouse.application.port.out;

/**
 * Output port for publishing domain events
 * This is a hexagonal architecture port that will be implemented by infrastructure layer
 */
public interface EventPublisher {
    
    /**
     * Publishes a domain event to the messaging system
     * 
     * @param event The domain event to publish
     */
    void publish(Object event);
    
    /**
     * Publishes multiple domain events in order
     * 
     * @param events The domain events to publish
     */
    default void publishAll(Object... events) {
        for (Object event : events) {
            publish(event);
        }
    }
}
