package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.Cliente;
import java.util.UUID;

/**
 * Use Case para buscar informações de cliente.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface BuscarClienteUseCase {
    
    /**
     * Busca cliente por ID.
     * 
     * @param clienteId ID do cliente
     * @return Cliente encontrado
     * @throws br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException se não existe
     */
    Cliente buscarPorId(UUID clienteId);
    
    /**
     * Busca cliente por email.
     * 
     * @param email Email do cliente
     * @return Cliente encontrado
     * @throws br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException se não existe
     */
    Cliente buscarPorEmail(String email);
    
    /**
     * Busca cliente por CPF.
     * 
     * @param cpf CPF do cliente
     * @return Cliente encontrado
     * @throws br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException se não existe
     */
    Cliente buscarPorCpf(String cpf);
}
