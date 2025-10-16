package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.DeliveryBox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for DeliveryBox
 * This is the infrastructure implementation
 * 
 * @author Franklin Canduri
 */
@Repository
public interface JpaDeliveryBoxRepository extends JpaRepository<DeliveryBox, UUID> {

    List<DeliveryBox> findByReceivedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT d FROM DeliveryBox d ORDER BY d.receivedAt DESC")
    List<DeliveryBox> findRecentDeliveries(Pageable pageable);
}
