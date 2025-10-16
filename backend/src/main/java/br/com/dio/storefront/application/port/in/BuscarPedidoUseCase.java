package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/**
 * Use Case para buscar pedidos.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface BuscarPedidoUseCase {
    
    /**
     * Busca pedido por ID.
     * 
     * @param pedidoId ID do pedido
     * @return Pedido encontrado
     * @throws br.com.dio.storefront.domain.exception.PedidoNaoEncontradoException se não existe
     */
    Pedido buscarPorId(UUID pedidoId);
    
    /**
     * Busca pedido por número.
     * 
     * @param numeroPedido Número do pedido (formato: PED-YYYYMMDD-XXXXX)
     * @return Pedido encontrado
     * @throws br.com.dio.storefront.domain.exception.PedidoNaoEncontradoException se não existe
     */
    Pedido buscarPorNumero(String numeroPedido);
    
    /**
     * Lista todos os pedidos de um cliente.
     * 
     * @param clienteId ID do cliente
     * @param pageable Configuração de paginação
     * @return Página de pedidos do cliente
     */
    Page<Pedido> listarPorCliente(UUID clienteId, Pageable pageable);
}
