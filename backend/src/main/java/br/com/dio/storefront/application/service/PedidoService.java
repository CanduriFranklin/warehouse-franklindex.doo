package br.com.dio.storefront.application.service;

import br.com.dio.storefront.application.port.in.BuscarPedidoUseCase;
import br.com.dio.storefront.domain.exception.PedidoNaoEncontradoException;
import br.com.dio.storefront.domain.model.Pedido;
import br.com.dio.storefront.domain.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service de Pedidos.
 * Implementa os Use Cases relacionados a consulta de pedidos.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Service
@Transactional(readOnly = true)
public class PedidoService implements BuscarPedidoUseCase {
    
    private final PedidoRepository pedidoRepository;
    
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
    
    @Override
    public Pedido buscarPorId(UUID pedidoId) {
        Objects.requireNonNull(pedidoId, "ID do pedido não pode ser null");
        
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
    }
    
    @Override
    public Pedido buscarPorNumero(String numeroPedido) {
        Objects.requireNonNull(numeroPedido, "Número do pedido não pode ser null");
        
        return pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado: " + numeroPedido));
    }
    
    @Override
    public Page<Pedido> listarPorCliente(UUID clienteId, Pageable pageable) {
        Objects.requireNonNull(clienteId, "ID do cliente não pode ser null");
        Objects.requireNonNull(pageable, "Pageable não pode ser null");
        
        return pedidoRepository.findByClienteId(clienteId, pageable);
    }
}
