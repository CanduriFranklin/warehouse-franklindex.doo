package br.com.dio.storefront.infrastructure.web.dto.response;

import br.com.dio.storefront.infrastructure.web.dto.DinheiroDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO para carrinho de compras.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record CarrinhoResponse(
        UUID id,
        UUID clienteId,
        List<ItemCarrinhoResponse> itens,
        DinheiroDTO valorTotal,
        String status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
