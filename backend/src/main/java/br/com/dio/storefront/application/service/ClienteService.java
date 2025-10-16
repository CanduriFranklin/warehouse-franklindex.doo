package br.com.dio.storefront.application.service;

import br.com.dio.storefront.application.port.in.BuscarClienteUseCase;
import br.com.dio.storefront.application.port.in.CadastrarClienteUseCase;
import br.com.dio.storefront.application.port.out.PublicarEventoPort;
import br.com.dio.storefront.domain.event.ClienteCadastradoEvent;
import br.com.dio.storefront.domain.exception.ClienteNaoEncontradoException;
import br.com.dio.storefront.domain.exception.StorefrontDomainException;
import br.com.dio.storefront.domain.model.Cliente;
import br.com.dio.storefront.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service de Clientes.
 * Implementa os Use Cases relacionados a cadastro e busca de clientes.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@Service
@Transactional
public class ClienteService implements CadastrarClienteUseCase, BuscarClienteUseCase {
    
    private final ClienteRepository clienteRepository;
    private final PublicarEventoPort publicarEventoPort;
    
    public ClienteService(
            ClienteRepository clienteRepository,
            PublicarEventoPort publicarEventoPort) {
        this.clienteRepository = clienteRepository;
        this.publicarEventoPort = publicarEventoPort;
    }
    
    @Override
    public UUID cadastrar(CadastrarClienteCommand command) {
        Objects.requireNonNull(command, "Command não pode ser null");
        
        // Valida email único
        if (clienteRepository.findByEmail(command.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + command.email());
        }
        
        // Valida CPF único
        if (clienteRepository.findByCpf(command.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado: " + command.cpf());
        }
        
        // Cria cliente
        Cliente cliente = Cliente.criar(
                command.nomeCompleto(),
                command.email(),
                command.cpf(),
                command.telefone(),
                command.endereco()
        );
        
        cliente = clienteRepository.save(cliente);
        
        // Publica evento
        publicarEventoPort.publicar(new ClienteCadastradoEvent(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()
        ));
        
        return cliente.getId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(UUID clienteId) {
        Objects.requireNonNull(clienteId, "ID do cliente não pode ser null");
        
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException(clienteId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email) {
        Objects.requireNonNull(email, "Email não pode ser null");
        
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Email não encontrado: " + email));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        Objects.requireNonNull(cpf, "CPF não pode ser null");
        
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNaoEncontradoException("CPF não encontrado: " + cpf));
    }
}
