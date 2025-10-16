package br.com.dio.storefront.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para informações de pagamento.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record InformacaoPagamentoDTO(
        
        @NotBlank(message = "Número do cartão não pode ser vazio")
        @Pattern(regexp = "\\d{13,19}", message = "Número do cartão deve ter entre 13 e 19 dígitos")
        String numeroCartao,
        
        @NotBlank(message = "Nome do titular não pode ser vazio")
        @Size(max = 100, message = "Nome do titular deve ter no máximo 100 caracteres")
        String nomeTitular,
        
        @NotNull(message = "Mês de validade não pode ser null")
        Integer mesValidade,
        
        @NotNull(message = "Ano de validade não pode ser null")
        Integer anoValidade,
        
        @NotBlank(message = "CVV não pode ser vazio")
        @Pattern(regexp = "\\d{3,4}", message = "CVV deve ter 3 ou 4 dígitos")
        String cvv
) {
}
