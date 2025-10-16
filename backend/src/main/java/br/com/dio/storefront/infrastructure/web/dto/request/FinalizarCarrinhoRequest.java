package br.com.dio.storefront.infrastructure.web.dto.request;

import br.com.dio.storefront.infrastructure.web.dto.EnderecoDTO;
import br.com.dio.storefront.infrastructure.web.dto.InformacaoPagamentoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO para finalizar carrinho (checkout).
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record FinalizarCarrinhoRequest(
        
        @NotNull(message = "Endereço de entrega não pode ser null")
        @Valid
        EnderecoDTO enderecoEntrega,
        
        @NotNull(message = "Informação de pagamento não pode ser null")
        @Valid
        InformacaoPagamentoDTO informacaoPagamento
) {
}
