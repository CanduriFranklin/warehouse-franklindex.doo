package br.com.dio.storefront.infrastructure.web.controller;

import br.com.dio.storefront.application.port.in.*;
import br.com.dio.storefront.domain.model.CarrinhoCompras;
import br.com.dio.storefront.infrastructure.web.dto.request.AdicionarProdutoAoCarrinhoRequest;
import br.com.dio.storefront.infrastructure.web.dto.request.AtualizarQuantidadeRequest;
import br.com.dio.storefront.infrastructure.web.dto.request.FinalizarCarrinhoRequest;
import br.com.dio.storefront.infrastructure.web.dto.response.CarrinhoResponse;
import br.com.dio.storefront.infrastructure.web.mapper.StorefrontMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Controller REST para operações de Carrinho de Compras.
 * Expõe endpoints para gerenciamento do carrinho (adicionar, remover, atualizar, finalizar).
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@RestController
@RequestMapping("/api/v1/clientes/{clienteId}/carrinho")
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
public class CarrinhoController {
    
    private final AdicionarProdutoAoCarrinhoUseCase adicionarProdutoUseCase;
    private final RemoverProdutoDoCarrinhoUseCase removerProdutoUseCase;
    private final AtualizarQuantidadeCarrinhoUseCase atualizarQuantidadeUseCase;
    private final ObterCarrinhoUseCase obterCarrinhoUseCase;
    private final FinalizarCarrinhoUseCase finalizarCarrinhoUseCase;
    private final StorefrontMapper mapper;
    
    public CarrinhoController(
            AdicionarProdutoAoCarrinhoUseCase adicionarProdutoUseCase,
            RemoverProdutoDoCarrinhoUseCase removerProdutoUseCase,
            AtualizarQuantidadeCarrinhoUseCase atualizarQuantidadeUseCase,
            ObterCarrinhoUseCase obterCarrinhoUseCase,
            FinalizarCarrinhoUseCase finalizarCarrinhoUseCase,
            StorefrontMapper mapper) {
        this.adicionarProdutoUseCase = adicionarProdutoUseCase;
        this.removerProdutoUseCase = removerProdutoUseCase;
        this.atualizarQuantidadeUseCase = atualizarQuantidadeUseCase;
        this.obterCarrinhoUseCase = obterCarrinhoUseCase;
        this.finalizarCarrinhoUseCase = finalizarCarrinhoUseCase;
        this.mapper = mapper;
    }
    
    @GetMapping
    @Operation(summary = "Obter carrinho ativo", description = "Retorna o carrinho ativo do cliente (cria se não existir)")
    public ResponseEntity<CarrinhoResponse> obterCarrinho(@PathVariable UUID clienteId) {
        CarrinhoCompras carrinho = obterCarrinhoUseCase.obter(clienteId);
        return ResponseEntity.ok(mapper.toResponse(carrinho));
    }
    
    @PostMapping("/itens")
    @Operation(summary = "Adicionar produto ao carrinho", description = "Adiciona um produto ao carrinho do cliente")
    public ResponseEntity<CarrinhoResponse> adicionarProduto(
            @PathVariable UUID clienteId,
            @Valid @RequestBody AdicionarProdutoAoCarrinhoRequest request) {
        
        AdicionarProdutoAoCarrinhoUseCase.AdicionarProdutoCommand command =
                new AdicionarProdutoAoCarrinhoUseCase.AdicionarProdutoCommand(
                        clienteId,
                        request.produtoId(),
                        request.quantidade()
                );
        
        UUID carrinhoId = adicionarProdutoUseCase.adicionar(command);
        CarrinhoCompras carrinho = obterCarrinhoUseCase.obter(clienteId);
        
        return ResponseEntity.ok(mapper.toResponse(carrinho));
    }
    
    @DeleteMapping("/itens/{produtoId}")
    @Operation(summary = "Remover produto do carrinho", description = "Remove um produto do carrinho do cliente")
    public ResponseEntity<CarrinhoResponse> removerProduto(
            @PathVariable UUID clienteId,
            @PathVariable UUID produtoId) {
        
        RemoverProdutoDoCarrinhoUseCase.RemoverProdutoCommand command =
                new RemoverProdutoDoCarrinhoUseCase.RemoverProdutoCommand(
                        clienteId,
                        produtoId
                );
        
        removerProdutoUseCase.remover(command);
        CarrinhoCompras carrinho = obterCarrinhoUseCase.obter(clienteId);
        
        return ResponseEntity.ok(mapper.toResponse(carrinho));
    }
    
    @PutMapping("/itens/{produtoId}/quantidade")
    @Operation(summary = "Atualizar quantidade de produto", description = "Atualiza a quantidade de um produto no carrinho")
    public ResponseEntity<CarrinhoResponse> atualizarQuantidade(
            @PathVariable UUID clienteId,
            @PathVariable UUID produtoId,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        
        AtualizarQuantidadeCarrinhoUseCase.AtualizarQuantidadeCommand command =
                new AtualizarQuantidadeCarrinhoUseCase.AtualizarQuantidadeCommand(
                        clienteId,
                        produtoId,
                        request.novaQuantidade()
                );
        
        atualizarQuantidadeUseCase.atualizar(command);
        CarrinhoCompras carrinho = obterCarrinhoUseCase.obter(clienteId);
        
        return ResponseEntity.ok(mapper.toResponse(carrinho));
    }
    
    @PostMapping("/finalizar")
    @Operation(summary = "Finalizar carrinho (checkout)", description = "Finaliza a compra criando um pedido")
    public ResponseEntity<UUID> finalizarCarrinho(
            @PathVariable UUID clienteId,
            @Valid @RequestBody FinalizarCarrinhoRequest request) {
        
        FinalizarCarrinhoUseCase.FinalizarCarrinhoCommand command =
                new FinalizarCarrinhoUseCase.FinalizarCarrinhoCommand(
                        clienteId,
                        mapper.toDomain(request.enderecoEntrega()),
                        mapper.toDomain(request.informacaoPagamento())
                );
        
        UUID pedidoId = finalizarCarrinhoUseCase.finalizar(command);
        
        return ResponseEntity
                .created(URI.create("/api/v1/pedidos/" + pedidoId))
                .body(pedidoId);
    }
}
