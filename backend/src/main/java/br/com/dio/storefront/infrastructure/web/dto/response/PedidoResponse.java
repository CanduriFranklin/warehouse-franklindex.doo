package br.com.dio.storefront.infrastructure.web.dto.response;

import br.com.dio.storefront.infrastructure.web.dto.DinheiroDTO;
import br.com.dio.storefront.infrastructure.web.dto.EnderecoDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO para pedido.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record PedidoResponse(
        UUID id,
        String numeroPedido,
        UUID clienteId,
        List<ItemPedidoResponse> itens,
        DinheiroDTO valorTotal,
        EnderecoDTO enderecoEntrega,
        String status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
