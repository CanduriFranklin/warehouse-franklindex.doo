package br.com.dio.storefront.infrastructure.web.dto.response;

import br.com.dio.storefront.infrastructure.web.dto.EnderecoDTO;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para cliente.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record ClienteResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String cpf,
        String telefone,
        EnderecoDTO endereco,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
