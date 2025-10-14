package br.com.dio.warehouse.domain.repository;

import br.com.dio.warehouse.domain.model.DeliveryBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DeliveryBox aggregate
 * This is a domain interface, not a Spring Data repository
 * 
 * @author Franklin Canduri
 */
public interface DeliveryBoxRepository {

    DeliveryBox save(DeliveryBox deliveryBox);

    Optional<DeliveryBox> findById(UUID id);

    List<DeliveryBox> findAll();

    Page<DeliveryBox> findAll(Pageable pageable);

    void delete(DeliveryBox deliveryBox);

    boolean existsById(UUID id);

    long count();

    // Business Queries

    List<DeliveryBox> findByReceivedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<DeliveryBox> findRecentDeliveries(int limit);
}
