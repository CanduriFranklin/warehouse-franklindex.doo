package br.com.dio.storefront.application.port.in;

import br.com.dio.storefront.domain.model.Cliente;
import br.com.dio.storefront.domain.valueobject.Endereco;
import java.util.UUID;

/**
 * Use Case para cadastrar novo cliente.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
public interface CadastrarClienteUseCase {
    
    /**
     * Cadastra um novo cliente no sistema.
     * Valida unicidade de email e CPF.
     * Emite evento ClienteCadastradoEvent.
     * 
     * @param command Comando com dados do cliente
     * @return ID do cliente criado
     * @throws IllegalArgumentException se email ou CPF já cadastrados
     */
    UUID cadastrar(CadastrarClienteCommand command);
    
    /**
     * Comando para cadastrar cliente.
     * 
     * @param nomeCompleto Nome completo do cliente
     * @param email Email único
     * @param cpf CPF único (será validado)
     * @param telefone Telefone de contato
     * @param endereco Endereço principal
     */
    record CadastrarClienteCommand(
        String nomeCompleto,
        String email,
        String cpf,
        String telefone,
        Endereco endereco
    ) {
        public CadastrarClienteCommand {
            if (nomeCompleto == null || nomeCompleto.isBlank()) {
                throw new IllegalArgumentException("Nome completo é obrigatório");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email é obrigatório");
            }
            if (cpf == null || cpf.isBlank()) {
                throw new IllegalArgumentException("CPF é obrigatório");
            }
            if (endereco == null) {
                throw new IllegalArgumentException("Endereço é obrigatório");
            }
        }
    }
}
