package br.com.dio.storefront.infrastructure.web.controller;

import br.com.dio.storefront.application.port.in.BuscarProdutoUseCase;
import br.com.dio.storefront.application.port.in.ListarProdutosUseCase;
import br.com.dio.storefront.domain.model.Produto;
import br.com.dio.storefront.infrastructure.web.dto.response.ProdutoResponse;
import br.com.dio.storefront.infrastructure.web.mapper.StorefrontMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para operações de Produtos (Catálogo).
 * Expõe endpoints para listagem e busca de produtos.
 * 
 * @author Franklin Canduri
 * @since 15/10/2025
 */
@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Endpoints para catálogo de produtos")
public class ProdutoController {
    
    private final ListarProdutosUseCase listarProdutosUseCase;
    private final BuscarProdutoUseCase buscarProdutoUseCase;
    private final StorefrontMapper mapper;
    
    public ProdutoController(
            ListarProdutosUseCase listarProdutosUseCase,
            BuscarProdutoUseCase buscarProdutoUseCase,
            StorefrontMapper mapper) {
        this.listarProdutosUseCase = listarProdutosUseCase;
        this.buscarProdutoUseCase = buscarProdutoUseCase;
        this.mapper = mapper;
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna lista paginada de produtos ativos")
    public ResponseEntity<Page<ProdutoResponse>> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        
        Page<Produto> produtos = listarProdutosUseCase.listar(pageable);
        Page<ProdutoResponse> response = produtos.map(mapper::toResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar produtos por categoria", description = "Retorna produtos filtrados por categoria")
    public ResponseEntity<Page<ProdutoResponse>> listarPorCategoria(
            @PathVariable String categoria,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        
        Page<Produto> produtos = listarProdutosUseCase.listarPorCategoria(categoria, pageable);
        Page<ProdutoResponse> response = produtos.map(mapper::toResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos por termo no nome (case insensitive)")
    public ResponseEntity<Page<ProdutoResponse>> buscarPorNome(
            @RequestParam String termo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        
        Page<Produto> produtos = listarProdutosUseCase.buscarPorNome(termo, pageable);
        Page<ProdutoResponse> response = produtos.map(mapper::toResponse);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna detalhes de um produto específico")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable UUID id) {
        Produto produto = buscarProdutoUseCase.buscar(id);
        return ResponseEntity.ok(mapper.toResponse(produto));
    }
}
