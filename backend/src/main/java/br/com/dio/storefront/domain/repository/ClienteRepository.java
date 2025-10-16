package br.com.dio.storefront.domain.repository;

import br.com.dio.storefront.domain.model.Cliente;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface de repositório para Cliente (Domain Layer).
 * Define o contrato para persistência de clientes.
 * Usa naming conventions do Spring Data JPA.
 */
public interface ClienteRepository {

    /**
     * Salva um cliente.
     */
    Cliente save(Cliente cliente);

    /**
     * Busca um cliente por ID.
     */
    Optional<Cliente> findById(UUID id);

    /**
     * Busca um cliente por email.
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca um cliente por CPF.
     */
    Optional<Cliente> findByCpf(String cpf);

    /**
     * Verifica se um cliente existe.
     */
    boolean existsById(UUID id);

    /**
     * Verifica se existe um cliente com o email informado.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe um cliente com o CPF informado.
     */
    boolean existsByCpf(String cpf);

    /**
     * Remove um cliente.
     */
    void deleteById(UUID id);
}
