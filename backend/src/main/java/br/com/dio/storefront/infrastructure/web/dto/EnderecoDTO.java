package br.com.dio.storefront.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para representação de endereços.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record EnderecoDTO(
        
        @NotBlank(message = "Logradouro não pode ser vazio")
        @Size(max = 200, message = "Logradouro deve ter no máximo 200 caracteres")
        String logradouro,
        
        @NotBlank(message = "Número não pode ser vazio")
        @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
        String numero,
        
        @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
        String complemento,
        
        @NotBlank(message = "Bairro não pode ser vazio")
        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        String bairro,
        
        @NotBlank(message = "Cidade não pode ser vazia")
        @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
        String cidade,
        
        @NotBlank(message = "Estado não pode ser vazio")
        @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres (UF)")
        String estado,
        
        @NotBlank(message = "CEP não pode ser vazio")
        @Pattern(regexp = "\\d{8}", message = "CEP deve ter 8 dígitos")
        String cep
) {
}
