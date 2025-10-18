package br.com.dio.storefront.infrastructure.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import br.com.dio.storefront.application.port.out.ValidarEstoquePort;
import br.com.dio.storefront.domain.model.Produto;

/**
 * Implementação simplificada de ValidarEstoquePort.
 * Em produção, poderia integrar com sistema externo de estoque (Warehouse).
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Component
public class EstoqueValidator implements ValidarEstoquePort {
    
    // Simula estoque reservado (em produção, seria em banco de dados ou cache distribuído)
    private final Map<UUID, Integer> estoqueReservado = new ConcurrentHashMap<>();
    
    @Override
    public boolean temEstoqueSuficiente(Produto produto, int quantidade) {
        int reservado = estoqueReservado.getOrDefault(produto.getId(), 0);
        int disponivelReal = produto.getQuantidadeEstoque() - reservado;
        return disponivelReal >= quantidade;
    }
    
    @Override
    public void reservarEstoque(UUID produtoId, int quantidade) {
        estoqueReservado.merge(produtoId, quantidade, Integer::sum);
    }
    
    @Override
    public void liberarEstoque(UUID produtoId, int quantidade) {
        estoqueReservado.computeIfPresent(produtoId, (_, reservado) -> {
            int novoValor = reservado - quantidade;
            return novoValor <= 0 ? null : novoValor;
        });
    }
}
