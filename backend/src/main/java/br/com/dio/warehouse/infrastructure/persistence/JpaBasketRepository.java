package br.com.dio.warehouse.infrastructure.persistence;

import br.com.dio.warehouse.domain.model.BasicBasket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository for BasicBasket
 * This is the infrastructure implementation
 * 
 * @author Franklin Canduri
 */
@Repository
public interface JpaBasketRepository extends JpaRepository<BasicBasket, UUID> {

    List<BasicBasket> findByStatus(BasicBasket.BasketStatus status);

    @Query("SELECT b FROM BasicBasket b WHERE b.status = 'AVAILABLE' AND b.validationDate >= CURRENT_DATE")
    List<BasicBasket> findAvailableBaskets();

    @Query("SELECT b FROM BasicBasket b WHERE b.validationDate < CURRENT_DATE AND b.status != 'DISPOSED'")
    List<BasicBasket> findExpiredBaskets();

    List<BasicBasket> findByValidationDateBefore(LocalDate date);

    List<BasicBasket> findByValidationDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM BasicBasket b WHERE b.status = 'AVAILABLE' AND b.validationDate >= CURRENT_DATE ORDER BY b.price.amount ASC")
    Page<BasicBasket> findCheapestAvailableBaskets(Pageable pageable);

    long countByStatus(BasicBasket.BasketStatus status);

    @Query("SELECT COUNT(b) FROM BasicBasket b WHERE b.validationDate < CURRENT_DATE AND b.status != 'DISPOSED'")
    long countExpiredBaskets();

    @Query("SELECT COUNT(b) FROM BasicBasket b WHERE b.status = 'AVAILABLE' AND b.validationDate >= CURRENT_DATE")
    long countAvailableBaskets();
}
