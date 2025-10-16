package br.com.dio.storefront.infrastructure.web.dto.response;

import br.com.dio.storefront.infrastructure.web.dto.DinheiroDTO;
import java.util.UUID;

/**
 * Response DTO para item do carrinho.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record ItemCarrinhoResponse(
        UUID produtoId,
        String nomeProduto,
        DinheiroDTO precoUnitario,
        Integer quantidade,
        DinheiroDTO subtotal
) {
}
