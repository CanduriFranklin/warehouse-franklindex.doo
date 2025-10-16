package br.com.dio.warehouse.domain.repository;

import br.com.dio.warehouse.domain.model.BasicBasket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BasicBasket aggregate
 * This is a domain interface, not a Spring Data repository
 * 
 * @author Franklin Canduri
 */
public interface BasketRepository {

    BasicBasket save(BasicBasket basket);

    List<BasicBasket> saveAll(List<BasicBasket> baskets);

    Optional<BasicBasket> findById(UUID id);

    List<BasicBasket> findAll();

    Page<BasicBasket> findAll(Pageable pageable);

    void delete(BasicBasket basket);

    void deleteAll(List<BasicBasket> baskets);

    boolean existsById(UUID id);

    long count();

    // Business Queries

    List<BasicBasket> findAvailableBaskets();

    List<BasicBasket> findExpiredBaskets();

    List<BasicBasket> findByStatus(BasicBasket.BasketStatus status);

    List<BasicBasket> findByValidationDateBefore(LocalDate date);

    List<BasicBasket> findByValidationDateBetween(LocalDate startDate, LocalDate endDate);

    List<BasicBasket> findCheapestAvailableBaskets(int limit);

    long countByStatus(BasicBasket.BasketStatus status);

    long countExpiredBaskets();

    long countAvailableBaskets();
}
