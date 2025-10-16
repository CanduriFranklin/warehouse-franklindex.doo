package br.com.dio.storefront.infrastructure.web.dto.request;

import br.com.dio.storefront.infrastructure.web.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO para cadastrar cliente.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record CadastrarClienteRequest(
        
        @NotBlank(message = "Nome completo não pode ser vazio")
        @Size(min = 3, max = 200, message = "Nome completo deve ter entre 3 e 200 caracteres")
        String nomeCompleto,
        
        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Email deve ser válido")
        String email,
        
        @NotBlank(message = "CPF não pode ser vazio")
        @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
        String cpf,
        
        @NotBlank(message = "Telefone não pode ser vazio")
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos")
        String telefone,
        
        @NotNull(message = "Endereço não pode ser null")
        @Valid
        EnderecoDTO endereco
) {
}
