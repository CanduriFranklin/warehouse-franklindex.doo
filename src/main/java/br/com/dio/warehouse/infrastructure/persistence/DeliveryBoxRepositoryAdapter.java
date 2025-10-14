package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.DeliveryBox;
import br.com.dio.warehouse.domain.repository.DeliveryBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain DeliveryBoxRepository interface
 * using Spring Data JPA
 * 
 * @author Franklin Canduri
 */
@Component
@RequiredArgsConstructor
public class DeliveryBoxRepositoryAdapter implements DeliveryBoxRepository {

    private final JpaDeliveryBoxRepository jpaRepository;

    @Override
    public DeliveryBox save(DeliveryBox deliveryBox) {
        return jpaRepository.save(deliveryBox);
    }

    @Override
    public Optional<DeliveryBox> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DeliveryBox> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<DeliveryBox> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public void delete(DeliveryBox deliveryBox) {
        jpaRepository.delete(deliveryBox);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<DeliveryBox> findByReceivedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByReceivedAtBetween(startDate, endDate);
    }

    @Override
    public List<DeliveryBox> findRecentDeliveries(int limit) {
        return jpaRepository.findRecentDeliveries(PageRequest.of(0, limit));
    }
}
