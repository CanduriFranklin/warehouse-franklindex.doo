package br.com.dio.storefront.infrastructure.web.dto.response;

import br.com.dio.storefront.infrastructure.web.dto.DinheiroDTO;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para produto.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public record ProdutoResponse(
        UUID id,
        String nome,
        String descricao,
        DinheiroDTO preco,
        String categoria,
        Integer quantidadeEstoque,
        boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}
