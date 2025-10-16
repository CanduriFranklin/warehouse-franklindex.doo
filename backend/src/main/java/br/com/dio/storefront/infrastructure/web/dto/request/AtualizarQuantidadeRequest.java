package br.com.dio.storefront.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO para atualizar quantidade de produto no carrinho.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record AtualizarQuantidadeRequest(
        
        @NotNull(message = "Nova quantidade não pode ser null")
        @Min(value = 1, message = "Nova quantidade deve ser >= 1")
        Integer novaQuantidade
) {
}
