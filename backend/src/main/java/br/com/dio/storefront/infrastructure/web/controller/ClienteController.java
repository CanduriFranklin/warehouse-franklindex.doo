package br.com.dio.storefront.infrastructure.web.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dio.storefront.application.port.in.BuscarClienteUseCase;
import br.com.dio.storefront.application.port.in.CadastrarClienteUseCase;
import br.com.dio.storefront.domain.model.Cliente;
import br.com.dio.storefront.infrastructure.web.dto.request.CadastrarClienteRequest;
import br.com.dio.storefront.infrastructure.web.dto.response.ClienteResponse;
import br.com.dio.storefront.infrastructure.web.mapper.StorefrontMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para operações de Clientes.
 * Expõe endpoints para cadastro e busca de clientes.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {
    
    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final StorefrontMapper mapper;
    
    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            BuscarClienteUseCase buscarClienteUseCase,
            StorefrontMapper mapper) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(summary = "Cadastrar novo cliente", description = "Cria um novo cliente no sistema")
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody CadastrarClienteRequest request) {
        
        CadastrarClienteUseCase.CadastrarClienteCommand command = 
                new CadastrarClienteUseCase.CadastrarClienteCommand(
                        request.nomeCompleto(),
                        request.email(),
                        request.cpf(),
                        request.telefone(),
                        mapper.toDomain(request.endereco())
                );
        
        UUID clienteId = cadastrarClienteUseCase.cadastrar(command);
        Cliente cliente = buscarClienteUseCase.buscarPorId(clienteId);
        
        return ResponseEntity
                .created(URI.create("/api/v1/clientes/" + clienteId))
                .body(mapper.toResponse(cliente));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna detalhes de um cliente específico")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        Cliente cliente = buscarClienteUseCase.buscarPorId(id);
        return ResponseEntity.ok(mapper.toResponse(cliente));
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar cliente por email", description = "Retorna cliente pelo endereço de email")
    public ResponseEntity<ClienteResponse> buscarPorEmail(@PathVariable String email) {
        Cliente cliente = buscarClienteUseCase.buscarPorEmail(email);
        return ResponseEntity.ok(mapper.toResponse(cliente));
    }
    
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar cliente por CPF", description = "Retorna cliente pelo número do CPF")
    public ResponseEntity<ClienteResponse> buscarPorCpf(@PathVariable String cpf) {
        Cliente cliente = buscarClienteUseCase.buscarPorCpf(cpf);
        return ResponseEntity.ok(mapper.toResponse(cliente));
    }
}
