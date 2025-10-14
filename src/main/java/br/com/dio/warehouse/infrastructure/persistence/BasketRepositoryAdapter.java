package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.BasicBasket;
import br.com.dio.warehouse.domain.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter that implements the domain BasketRepository interface
 * using Spring Data JPA
 * 
 * @author Franklin Canduri
 */
@Component
@RequiredArgsConstructor
public class BasketRepositoryAdapter implements BasketRepository {

    private final JpaBasketRepository jpaRepository;

    @Override
    public BasicBasket save(BasicBasket basket) {
        return jpaRepository.save(basket);
    }

    @Override
    public List<BasicBasket> saveAll(List<BasicBasket> baskets) {
        return jpaRepository.saveAll(baskets);
    }

    @Override
    public Optional<BasicBasket> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<BasicBasket> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Page<BasicBasket> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public void delete(BasicBasket basket) {
        jpaRepository.delete(basket);
    }

    @Override
    public void deleteAll(List<BasicBasket> baskets) {
        jpaRepository.deleteAll(baskets);
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
    public List<BasicBasket> findAvailableBaskets() {
        return jpaRepository.findAvailableBaskets();
    }

    @Override
    public List<BasicBasket> findExpiredBaskets() {
        return jpaRepository.findExpiredBaskets();
    }

    @Override
    public List<BasicBasket> findByStatus(BasicBasket.BasketStatus status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<BasicBasket> findByValidationDateBefore(LocalDate date) {
        return jpaRepository.findByValidationDateBefore(date);
    }

    @Override
    public List<BasicBasket> findByValidationDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByValidationDateBetween(startDate, endDate);
    }

    @Override
    public List<BasicBasket> findCheapestAvailableBaskets(int limit) {
        return jpaRepository.findCheapestAvailableBaskets(PageRequest.of(0, limit))
                .getContent();
    }

    @Override
    public long countByStatus(BasicBasket.BasketStatus status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public long countExpiredBaskets() {
        return jpaRepository.countExpiredBaskets();
    }

    @Override
    public long countAvailableBaskets() {
        return jpaRepository.countAvailableBaskets();
    }
}
