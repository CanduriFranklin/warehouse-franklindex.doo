package br.com.dio.storefront.infrastructure.web.controller;

import br.com.dio.storefront.application.port.in.BuscarPedidoUseCase;
import br.com.dio.storefront.domain.model.Pedido;
import br.com.dio.storefront.infrastructure.web.dto.response.PedidoResponse;
import br.com.dio.storefront.infrastructure.web.mapper.StorefrontMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para operações de Pedidos.
 * Expõe endpoints para consulta de pedidos.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para consulta de pedidos")
public class PedidoController {
    
    private final BuscarPedidoUseCase buscarPedidoUseCase;
    private final StorefrontMapper mapper;
    
    public PedidoController(
            BuscarPedidoUseCase buscarPedidoUseCase,
            StorefrontMapper mapper) {
        this.buscarPedidoUseCase = buscarPedidoUseCase;
        this.mapper = mapper;
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna detalhes de um pedido específico")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable UUID id) {
        Pedido pedido = buscarPedidoUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }
    
    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Buscar pedido por número", description = "Retorna pedido pelo número único")
    public ResponseEntity<PedidoResponse> buscarPorNumero(@PathVariable String numeroPedido) {
        Pedido pedido = buscarPedidoUseCase.buscarPorNumero(numeroPedido);
        return ResponseEntity.ok(mapper.toResponse(pedido));
    }
    
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos do cliente", description = "Retorna lista paginada de pedidos de um cliente")
    public ResponseEntity<Page<PedidoResponse>> listarPorCliente(
            @PathVariable UUID clienteId,
            @PageableDefault(size = 20, sort = "criadoEm") Pageable pageable) {
        
        Page<Pedido> pedidos = buscarPedidoUseCase.listarPorCliente(clienteId, pageable);
        Page<PedidoResponse> response = pedidos.map(mapper::toResponse);
        
        return ResponseEntity.ok(response);
    }
}
