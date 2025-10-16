package br.com.dio.storefront.domain.repository;

import br.com.dio.storefront.domain.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Pedido (Domain Layer).
 * Define o contrato para persistência de pedidos.
 * Usa naming conventions do Spring Data JPA.
 */
public interface PedidoRepository {

    /**
     * Salva um pedido.
     */
    Pedido save(Pedido pedido);

    /**
     * Busca um pedido por ID.
     */
    Optional<Pedido> findById(UUID id);

    /**
     * Busca um pedido por número.
     */
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    /**
     * Busca todos os pedidos de um cliente (paginado).
     */
    Page<Pedido> findByClienteId(UUID clienteId, Pageable pageable);

    /**
     * Busca pedidos de um cliente por status.
     */
    List<Pedido> findByClienteIdAndStatus(UUID clienteId, Pedido.StatusPedido status);

    /**
     * Busca todos os pedidos por status.
     */
    List<Pedido> findByStatus(Pedido.StatusPedido status);

    /**
     * Busca pedidos pendentes (aguardando pagamento ou pagamento confirmado).
     */
    List<Pedido> findByStatusIn(List<Pedido.StatusPedido> statuses);

    /**
     * Verifica se um pedido existe.
     */
    boolean existsById(UUID id);

    /**
     * Verifica se existe um pedido com o número informado.
     */
    boolean existsByNumeroPedido(String numeroPedido);
    
    /**
     * Remove um pedido.
     */
    void deleteById(UUID id);
}
