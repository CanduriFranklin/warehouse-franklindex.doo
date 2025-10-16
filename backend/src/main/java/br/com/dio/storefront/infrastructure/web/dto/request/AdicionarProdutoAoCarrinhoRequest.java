package br.com.dio.storefront.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request DTO para adicionar produto ao carrinho.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record AdicionarProdutoAoCarrinhoRequest(
        
        @NotNull(message = "ID do produto não pode ser null")
        UUID produtoId,
        
        @NotNull(message = "Quantidade não pode ser null")
        @Min(value = 1, message = "Quantidade deve ser >= 1")
        Integer quantidade
) {
}
